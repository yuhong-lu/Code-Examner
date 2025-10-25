// service/SubmissionService.java
package com.CodeExamner.service;

import com.CodeExamner.entity.Submission;
import com.CodeExamner.entity.enums.JudgeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubmissionService {
    Submission submitCode(Submission submission);
    Submission getSubmissionById(Long id);
    Page<Submission> getSubmissionsByUser(Pageable pageable);
    Page<Submission> getSubmissionsByProblem(Long problemId, Pageable pageable);
    Page<Submission> getSubmissionsByExam(Long examId, Pageable pageable);
    List<Submission> getUserSubmissionsInExam(Long examId, Long userId);
    void updateSubmissionResult(Long submissionId, JudgeStatus status, Integer score,
                                Integer timeUsed, Integer memoryUsed);
    Submission resubmit(Long submissionId);
}