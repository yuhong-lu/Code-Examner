// service/impl/JudgeServiceImpl.java
package com.CodeExamner.service.impl;

import com.CodeExamner.entity.Submission;
import com.CodeExamner.entity.SubmissionDetail;
import com.CodeExamner.entity.TestCase;
import com.CodeExamner.entity.enums.JudgeStatus;
import com.CodeExamner.judge0.Judge0Client;
import com.CodeExamner.judge0.Judge0Submission;
import com.CodeExamner.judge0.Judge0Result;
import com.CodeExamner.judge0.Judge0Status;
import com.CodeExamner.repository.SubmissionRepository;
import com.CodeExamner.repository.SubmissionDetailRepository;
import com.CodeExamner.repository.TestCaseRepository;
import com.CodeExamner.service.JudgeService;
import com.CodeExamner.util.CodeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JudgeServiceImpl implements JudgeService {

    private final Judge0Client judge0Client;
    private final SubmissionRepository submissionRepository;
    private final SubmissionDetailRepository submissionDetailRepository;
    private final TestCaseRepository testCaseRepository;
    private final CodeValidator codeValidator;

    // Judge0 语言ID映射
    private static final Map<String, Integer> LANGUAGE_MAP = Map.of(
            "java", 62,
            "python", 71,
            "cpp", 54,
            "c", 50,
            "javascript", 63,
            "go", 60,
            "rust", 73
    );

    @Override
    @Async
    @Transactional
    public void judgeSubmission(Submission submission) {
        try {
            log.info("开始评测提交: submissionId={}, language={}",
                    submission.getId(), submission.getLanguage());

            // 1. 代码安全性验证
            if (!validateCodeSafety(submission)) {
                handleSubmissionError(submission, JudgeStatus.SECURITY_ERROR, "代码安全性验证失败");
                return;
            }

            // 2. 获取测试用例
            List<TestCase> testCases = testCaseRepository.findByProblemId(submission.getProblem().getId());
            if (testCases.isEmpty()) {
                handleSubmissionError(submission, JudgeStatus.NO_TEST_CASES, "未找到测试用例");
                return;
            }

            // 3. 更新状态为评测中
            updateSubmissionStatus(submission, JudgeStatus.JUDGING, null);

            // 4. 为每个测试用例创建提交详情
            createSubmissionDetails(submission, testCases);

            // 5. 异步处理所有测试用例
            processAllTestCasesAsync(submission, testCases);

        } catch (Exception e) {
            log.error("评测提交失败 - submissionId: {}, error: {}",
                    submission.getId(), e.getMessage(), e);
            handleSubmissionError(submission, JudgeStatus.SYSTEM_ERROR, "系统错误: " + e.getMessage());
        }
    }

    /**
     * 异步处理所有测试用例
     */
    @Async
    public CompletableFuture<Void> processAllTestCasesAsync(Submission submission, List<TestCase> testCases) {
        try {
            List<CompletableFuture<Void>> futures = testCases.stream()
                    .map(testCase -> processSingleTestCaseAsync(submission, testCase))
                    .collect(Collectors.toList());

            // 等待所有测试用例完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenRun(() -> {
                        // 所有测试用例完成后，计算最终分数
                        calculateFinalScore(submission.getId());
                    })
                    .exceptionally(throwable -> {
                        log.error("处理测试用例时发生错误: {}", throwable.getMessage(), throwable);
                        return null;
                    });

        } catch (Exception e) {
            log.error("异步处理测试用例失败: {}", e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * 处理单个测试用例
     */
    @Async
    public CompletableFuture<Void> processSingleTestCaseAsync(Submission submission, TestCase testCase) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. 创建 Judge0 提交
                Judge0Submission judge0Submission = createJudge0Submission(submission, testCase);
                Judge0Submission submissionResponse = judge0Client.submitCode(judge0Submission);

                if (submissionResponse == null || submissionResponse.getToken() == null) {
                    updateSubmissionDetailStatus(submission.getId(), testCase.getId(),
                            JudgeStatus.SUBMISSION_ERROR, "提交到评测服务失败");
                    return null;
                }

                String token = submissionResponse.getToken();
                log.debug("测试用例提交成功 - submissionId: {}, testCaseId: {}, token: {}",
                        submission.getId(), testCase.getId(), token);

                // 2. 轮询获取结果（最多等待30秒）
                Judge0Result result = pollSubmissionResult(token, 30);

                if (result != null) {
                    updateSubmissionDetailWithResult(submission.getId(), testCase.getId(), result);
                } else {
                    updateSubmissionDetailStatus(submission.getId(), testCase.getId(),
                            JudgeStatus.TIME_LIMIT_EXCEEDED, "获取结果超时");
                }

            } catch (Exception e) {
                log.error("处理测试用例失败 - submissionId: {}, testCaseId: {}, error: {}",
                        submission.getId(), testCase.getId(), e.getMessage(), e);
                updateSubmissionDetailStatus(submission.getId(), testCase.getId(),
                        JudgeStatus.SYSTEM_ERROR, "系统错误: " + e.getMessage());
            }
            return null;
        });
    }

    /**
     * 轮询获取评测结果
     */
    private Judge0Result pollSubmissionResult(String token, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                Judge0Result result = judge0Client.getSubmissionResult(token);
                if (result != null && result.getStatus() != null) {
                    Integer statusId = result.getStatus().getId();

                    // 状态码说明:
                    // 1: In Queue, 2: Processing, 3+: 已完成
                    if (statusId > 2) {
                        return result;
                    }

                    // 如果还在队列中或处理中，记录状态
                    if (i % 5 == 0) { // 每5次记录一次日志
                        log.debug("评测进行中 - token: {}, status: {}", token, result.getStatus().getDescription());
                    }
                }

                // 等待1秒
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("轮询评测结果失败 - token: {}, error: {}", token, e.getMessage());
            }
        }
        return null;
    }

    /**
     * 创建 Judge0 提交对象
     */
    private Judge0Submission createJudge0Submission(Submission submission, TestCase testCase) {
        Judge0Submission judge0Submission = new Judge0Submission();
        judge0Submission.setSourceCode(submission.getCode());
        judge0Submission.setLanguageId(getLanguageId(submission.getLanguage()));
        judge0Submission.setStdin(testCase.getInput());
        judge0Submission.setExpectedOutput(testCase.getExpectedOutput());
        judge0Submission.setCpuTimeLimit(3.0); // 3秒时间限制
        judge0Submission.setMemoryLimit(256);  // 256MB内存限制
        return judge0Submission;
    }

    /**
     * 获取语言ID
     */
    private Integer getLanguageId(String language) {
        return LANGUAGE_MAP.getOrDefault(language.toLowerCase(), 62); // 默认Java
    }

    /**
     * 更新提交详情状态
     */
    @Transactional
    public void updateSubmissionDetailStatus(Long submissionId, Long testCaseId,
                                             JudgeStatus status, String errorMessage) {
        try {
            SubmissionDetail detail = submissionDetailRepository
                    .findBySubmissionIdAndTestCaseId(submissionId, testCaseId);

            if (detail != null) {
                detail.setStatus(status);
                if (errorMessage != null) {
                    detail.setErrorMessage(errorMessage);
                }
                submissionDetailRepository.save(detail);
                log.debug("更新提交详情状态 - submissionId: {}, testCaseId: {}, status: {}",
                        submissionId, testCaseId, status);
            }
        } catch (Exception e) {
            log.error("更新提交详情状态失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 使用 Judge0 结果更新提交详情
     */
    @Transactional
    public void updateSubmissionDetailWithResult(Long submissionId, Long testCaseId, Judge0Result result) {
        try {
            SubmissionDetail detail = submissionDetailRepository
                    .findBySubmissionIdAndTestCaseId(submissionId, testCaseId);

            if (detail != null) {
                // 映射状态
                JudgeStatus status = mapJudge0Status(result.getStatus().getId());
                detail.setStatus(status);

                // 设置输出和错误信息
                detail.setOutput(result.getStdout());

                String errorMessage = getErrorMessage(result);
                if (errorMessage != null) {
                    detail.setErrorMessage(errorMessage);
                }

                // 设置时间和内存使用
                if (result.getTime() != null) {
                    try {
                        double timeInSeconds = Double.parseDouble(result.getTime());
                        detail.setTimeUsed((int) (timeInSeconds * 1000)); // 转换为毫秒
                    } catch (NumberFormatException e) {
                        log.warn("时间格式解析失败: {}", result.getTime());
                    }
                }

                if (result.getMemory() != null) {
                    detail.setMemoryUsed(result.getMemory().intValue());
                }

                submissionDetailRepository.save(detail);
                log.info("测试用例评测完成 - submissionId: {}, testCaseId: {}, status: {}, time: {}ms",
                        submissionId, testCaseId, status, detail.getTimeUsed());
            }
        } catch (Exception e) {
            log.error("更新评测结果失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 计算最终分数
     */
    @Transactional
    public void calculateFinalScore(Long submissionId) {
        try {
            Submission submission = submissionRepository.findById(submissionId).orElse(null);
            if (submission == null) return;

            List<SubmissionDetail> details = submissionDetailRepository.findBySubmissionId(submissionId);

            long totalCases = details.size();
            long acceptedCases = details.stream()
                    .filter(d -> d.getStatus() == JudgeStatus.ACCEPTED)
                    .count();

            // 计算分数
            int score = totalCases > 0 ? (int) ((acceptedCases * 100) / totalCases) : 0;

            // 确定最终状态
            JudgeStatus finalStatus = determineFinalStatus(details);

            // 计算总时间和内存
            Integer totalTime = details.stream()
                    .map(SubmissionDetail::getTimeUsed)
                    .filter(time -> time != null)
                    .reduce(0, Integer::sum);

            Integer maxMemory = details.stream()
                    .map(SubmissionDetail::getMemoryUsed)
                    .filter(memory -> memory != null)
                    .max(Integer::compareTo)
                    .orElse(0);

            // 更新提交
            submission.setScore(score);
            submission.setStatus(finalStatus);
            submission.setTimeUsed(totalTime);
            submission.setMemoryUsed(maxMemory);

            submissionRepository.save(submission);

            log.info("评测完成 - submissionId: {}, score: {}/100, status: {}, time: {}ms, memory: {}KB",
                    submissionId, score, finalStatus, totalTime, maxMemory);

        } catch (Exception e) {
            log.error("计算最终分数失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 确定最终状态
     */
    private JudgeStatus determineFinalStatus(List<SubmissionDetail> details) {
        if (details.isEmpty()) {
            return JudgeStatus.NO_TEST_CASES;
        }

        boolean allAccepted = details.stream()
                .allMatch(d -> d.getStatus() == JudgeStatus.ACCEPTED);

        if (allAccepted) {
            return JudgeStatus.ACCEPTED;
        }

        // 如果有编译错误，优先显示
        boolean hasCompileError = details.stream()
                .anyMatch(d -> d.getStatus() == JudgeStatus.COMPILATION_ERROR);
        if (hasCompileError) {
            return JudgeStatus.COMPILATION_ERROR;
        }

        // 其他情况返回错误答案
        return JudgeStatus.WRONG_ANSWER;
    }

    /**
     * 获取错误信息
     */
    private String getErrorMessage(Judge0Result result) {
        if (result.getStderr() != null && !result.getStderr().trim().isEmpty()) {
            return result.getStderr();
        }
        if (result.getCompileOutput() != null && !result.getCompileOutput().trim().isEmpty()) {
            return result.getCompileOutput();
        }
        if (result.getMessage() != null && !result.getMessage().trim().isEmpty()) {
            return result.getMessage();
        }
        return null;
    }

    /**
     * 代码安全性验证
     */
    private boolean validateCodeSafety(Submission submission) {
        try {
            String language = submission.getLanguage().toLowerCase();
            String code = submission.getCode();

            if (code == null || code.trim().isEmpty()) {
                return false;
            }

            // Java代码验证
            if ("java".equals(language)) {
                return codeValidator.validateJavaCode(code);
            }

            // 其他语言的验证可以在这里添加
            return true;

        } catch (Exception e) {
            log.error("代码安全性验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 处理提交错误
     */
    private void handleSubmissionError(Submission submission, JudgeStatus status, String errorMessage) {
        submission.setStatus(status);
        submission.setScore(0);
        submissionRepository.save(submission);
        log.warn("提交处理失败 - submissionId: {}, status: {}, reason: {}",
                submission.getId(), status, errorMessage);
    }

    /**
     * 更新提交状态
     */
    private void updateSubmissionStatus(Submission submission, JudgeStatus status, String message) {
        submission.setStatus(status);
        submissionRepository.save(submission);
    }

    /**
     * 创建提交详情记录
     */
    private void createSubmissionDetails(Submission submission, List<TestCase> testCases) {
        for (TestCase testCase : testCases) {
            SubmissionDetail detail = new SubmissionDetail();
            detail.setSubmission(submission);
            detail.setTestCase(testCase);
            detail.setStatus(JudgeStatus.PENDING);
            submission.getDetails().add(detail);
        }
        submissionRepository.save(submission);
    }

    @Override
    public void processJudgeResult(Long submissionId, String judge0Token) {
        // 同步处理评测结果（用于回调）
        log.info("处理同步评测结果 - submissionId: {}, token: {}", submissionId, judge0Token);
    }

    /**
     * 映射 Judge0 状态到系统状态
     */
    private JudgeStatus mapJudge0Status(Integer judge0StatusId) {
        if (judge0StatusId == null) return JudgeStatus.RUNTIME_ERROR;

        switch (judge0StatusId) {
            case 3: return JudgeStatus.ACCEPTED;
            case 4: return JudgeStatus.WRONG_ANSWER;
            case 5: return JudgeStatus.TIME_LIMIT_EXCEEDED;
            case 6: return JudgeStatus.COMPILATION_ERROR;
            case 7: return JudgeStatus.RUNTIME_ERROR;
            case 8: return JudgeStatus.MEMORY_LIMIT_EXCEEDED;
            case 9: return JudgeStatus.RUNTIME_ERROR; // API Error
            case 10: return JudgeStatus.SYSTEM_ERROR; // Submission Error
            default: return JudgeStatus.SYSTEM_ERROR;
        }
    }
}