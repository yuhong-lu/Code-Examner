// service/impl/JudgeServiceImpl.java
package com.CodeExamner.service.impl;

import com.CodeExamner.entity.Submission;
import com.CodeExamner.entity.SubmissionDetail;
import com.CodeExamner.entity.TestCase;
import com.CodeExamner.entity.enums.JudgeStatus;
import com.CodeExamner.judge0.Judge0Client;
import com.CodeExamner.judge0.Judge0Submission;
import com.CodeExamner.judge0.Judge0Result;
import com.CodeExamner.repository.SubmissionRepository;
import com.CodeExamner.repository.TestCaseRepository;
import com.CodeExamner.service.JudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    private Judge0Client judge0Client;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Override
    @Async
    public void judgeSubmission(Submission submission) {
        try {
            // 获取题目的所有测试用例
            List<TestCase> testCases = testCaseRepository.findByProblemId(submission.getProblem().getId());

            if (testCases.isEmpty()) {
                submission.setStatus(JudgeStatus.RUNTIME_ERROR);
                submission.setScore(0);
                submissionRepository.save(submission);
                return;
            }

            // 更新提交状态为评测中
            submission.setStatus(JudgeStatus.JUDGING);
            submissionRepository.save(submission);

            int passedCases = 0;
            int totalScore = 0;

            // 对每个测试用例进行评测
            for (TestCase testCase : testCases) {
                Judge0Submission judge0Submission = new Judge0Submission();
                judge0Submission.setSourceCode(submission.getCode());
                judge0Submission.setLanguageId(62); // Java语言ID
                judge0Submission.setStdin(testCase.getInput());
                judge0Submission.setExpectedOutput(testCase.getExpectedOutput());
                judge0Submission.setCpuTimeLimit(2.0); // 2秒时间限制
                judge0Submission.setMemoryLimit(128); // 128MB内存限制

                // 提交到Judge0
                Judge0Submission result = judge0Client.submitCode(judge0Submission);

                if (result != null && result.getToken() != null) {
                    // 创建提交详情记录
                    SubmissionDetail detail = new SubmissionDetail();
                    detail.setSubmission(submission);
                    detail.setTestCase(testCase);
                    detail.setStatus(JudgeStatus.JUDGING);
                    submission.getDetails().add(detail);

                    // 异步处理评测结果
                    processJudgeResultAsync(submission.getId(), result.getToken(), testCase.getId());
                }
            }

        } catch (Exception e) {
            log.error("评测提交失败: {}", e.getMessage());
            submission.setStatus(JudgeStatus.RUNTIME_ERROR);
            submissionRepository.save(submission);
        }
    }

    @Async
    public CompletableFuture<Void> processJudgeResultAsync(Long submissionId, String token, Long testCaseId) {
        try {
            // 等待评测完成
            Thread.sleep(2000);

            Judge0Result result = judge0Client.getSubmissionResult(token);
            if (result != null && result.getStatus() != null) {
                updateSubmissionDetail(submissionId, testCaseId, result);
            }
        } catch (Exception e) {
            log.error("处理评测结果失败: {}", e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private void updateSubmissionDetail(Long submissionId, Long testCaseId, Judge0Result result) {
        Submission submission = submissionRepository.findById(submissionId).orElse(null);
        if (submission == null) return;

        SubmissionDetail detail = submission.getDetails().stream()
                .filter(d -> d.getTestCase().getId().equals(testCaseId))
                .findFirst()
                .orElse(null);

        if (detail != null) {
            // 根据Judge0状态更新评测状态
            JudgeStatus status = mapJudge0Status(result.getStatus().getId());
            detail.setStatus(status);
            detail.setOutput(result.getStdout());
            detail.setErrorMessage(result.getStderr() != null ? result.getStderr() : result.getCompileOutput());

            if (result.getTime() != null) {
                detail.setTimeUsed((int)(Double.parseDouble(result.getTime()) * 1000));
            }
            if (result.getMemory() != null) {
                detail.setMemoryUsed(result.getMemory().intValue());
            }

            submissionRepository.save(submission);
        }
    }

    @Override
    public void processJudgeResult(Long submissionId, String judge0Token) {
        // 实现同步处理评测结果
    }

    private JudgeStatus mapJudge0Status(Integer judge0StatusId) {
        if (judge0StatusId == null) return JudgeStatus.RUNTIME_ERROR;

        switch (judge0StatusId) {
            case 3: // Accepted
                return JudgeStatus.ACCEPTED;
            case 4: // Wrong Answer
                return JudgeStatus.WRONG_ANSWER;
            case 5: // Time Limit Exceeded
                return JudgeStatus.TIME_LIMIT_EXCEEDED;
            case 6: // Compilation Error
                return JudgeStatus.COMPILATION_ERROR;
            case 7: // Runtime Error
            case 8: // Memory Limit Exceeded
                return JudgeStatus.MEMORY_LIMIT_EXCEEDED;
            default:
                return JudgeStatus.RUNTIME_ERROR;
        }
    }
}