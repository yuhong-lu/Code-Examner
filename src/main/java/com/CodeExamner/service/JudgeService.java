// service/JudgeService.java
package com.CodeExamner.service;

import com.CodeExamner.entity.Submission;

public interface JudgeService {
    void judgeSubmission(Submission submission);
    void processJudgeResult(Long submissionId, String judge0Token);
}