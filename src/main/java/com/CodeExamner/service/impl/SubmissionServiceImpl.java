// service/impl/SubmissionServiceImpl.java (修复版本)
package com.CodeExamner.service.impl;

import com.CodeExamner.entity.*;
import com.CodeExamner.entity.enums.ExamStatus;
import com.CodeExamner.entity.enums.JudgeStatus;
import com.CodeExamner.repository.SubmissionRepository;
import com.CodeExamner.repository.ProblemRepository;
import com.CodeExamner.repository.ExamRepository;
import com.CodeExamner.service.SubmissionService;
import com.CodeExamner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserService userService;

    @Override
    public Submission submitCode(Submission submission) {
        User currentUser = userService.getCurrentUser();

        // 验证题目存在
        Problem problem = problemRepository.findById(submission.getProblem().getId())
                .orElseThrow(() -> new RuntimeException("题目不存在"));

        submission.setStudent((Student) currentUser);
        submission.setProblem(problem);
        submission.setStatus(JudgeStatus.PENDING);

        // 如果是考试提交，验证考试状态
        if (submission.getExam() != null && submission.getExam().getId() != null) {
            Exam exam = examRepository.findById(submission.getExam().getId())
                    .orElseThrow(() -> new RuntimeException("考试不存在"));

            if (exam.getStatus() != ExamStatus.ONGOING) {
                throw new RuntimeException("考试未在进行中");
            }

            // 检查考试时间 - 修复这里的逻辑
            if (!isWithinExamTime(exam)) {
                throw new RuntimeException("不在考试时间内");
            }

            submission.setExam(exam);
        }

        return submissionRepository.save(submission);
    }

    @Override
    public Submission getSubmissionById(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        // 检查访问权限
        User currentUser = userService.getCurrentUser();
        if (!submission.getStudent().getId().equals(currentUser.getId()) &&
                !canViewAllSubmissions(currentUser)) {
            throw new RuntimeException("无权查看此提交记录");
        }

        return submission;
    }

    @Override
    public Page<Submission> getSubmissionsByUser(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return submissionRepository.findByStudentId(currentUser.getId(), pageable);
    }

    @Override
    public Page<Submission> getSubmissionsByProblem(Long problemId, Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        // 学生只能看自己的提交，教师和管理员可以看所有
        if (currentUser.getRole().name().startsWith("ROLE_STUDENT")) {
            return submissionRepository.findByProblemIdAndStudentId(problemId, currentUser.getId(), pageable);
        } else {
            return submissionRepository.findByProblemId(problemId, pageable);
        }
    }

    @Override
    public Page<Submission> getSubmissionsByExam(Long examId, Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        // 学生只能看自己的考试提交
        if (currentUser.getRole().name().startsWith("ROLE_STUDENT")) {
            return submissionRepository.findByExamIdAndStudentId(examId, currentUser.getId(), pageable);
        } else {
            return submissionRepository.findByExamId(examId, pageable);
        }
    }

    @Override
    public List<Submission> getUserSubmissionsInExam(Long examId, Long userId) {
        User currentUser = userService.getCurrentUser();

        // 只能查看自己的提交，或者教师查看学生的提交
        if (!currentUser.getId().equals(userId) &&
                !canViewAllSubmissions(currentUser)) {
            throw new RuntimeException("无权查看此用户的提交记录");
        }

        return submissionRepository.findByExamIdAndStudentId(examId, userId);
    }

    @Override
    public void updateSubmissionResult(Long submissionId, JudgeStatus status,
                                       Integer score, Integer timeUsed, Integer memoryUsed) {
        Submission submission = getSubmissionById(submissionId);
        submission.setStatus(status);
        submission.setScore(score);
        submission.setTimeUsed(timeUsed);
        submission.setMemoryUsed(memoryUsed);

        submissionRepository.save(submission);
    }

    @Override
    public Submission resubmit(Long submissionId) {
        Submission original = getSubmissionById(submissionId);

        Submission newSubmission = new Submission();
        newSubmission.setProblem(original.getProblem());
        newSubmission.setExam(original.getExam());
        newSubmission.setStudent(original.getStudent());
        newSubmission.setCode(original.getCode());
        newSubmission.setLanguage(original.getLanguage());
        newSubmission.setStatus(JudgeStatus.PENDING);

        return submissionRepository.save(newSubmission);
    }

    // 修复这个方法 - 添加参数检查
    private boolean isWithinExamTime(Exam exam) {
        if (exam == null || exam.getStartTime() == null || exam.getEndTime() == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        return (now.isEqual(exam.getStartTime()) || now.isAfter(exam.getStartTime())) &&
                (now.isEqual(exam.getEndTime()) || now.isBefore(exam.getEndTime()));
    }

    private boolean canViewAllSubmissions(User user) {
        return user.getRole().name().startsWith("ROLE_TEACHER") ||
                user.getRole().name().startsWith("ROLE_ADMIN");
    }
}