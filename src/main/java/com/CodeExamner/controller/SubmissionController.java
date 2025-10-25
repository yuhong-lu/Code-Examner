// controller/SubmissionController.java
package com.CodeExamner.controller;

import com.CodeExamner.dto.request.SubmissionRequest;
import com.CodeExamner.dto.response.SubmissionResponse;
import com.CodeExamner.entity.Submission;
import com.CodeExamner.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submitCode(@Valid @RequestBody SubmissionRequest request) {
        Submission submission = new Submission();
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());

        // 设置关联的题目
        com.CodeExamner.entity.Problem problem = new com.CodeExamner.entity.Problem();
        problem.setId(request.getProblemId());
        submission.setProblem(problem);

        // 如果是考试提交，设置考试
        if (request.getExamId() != null) {
            com.CodeExamner.entity.Exam exam = new com.CodeExamner.entity.Exam();
            exam.setId(request.getExamId());
            submission.setExam(exam);
        }

        Submission created = submissionService.submitCode(submission);
        return ResponseEntity.ok(convertToResponse(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionResponse> getSubmission(@PathVariable Long id) {
        Submission submission = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(convertToResponse(submission));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<SubmissionResponse>> getMySubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submitTime").descending());
        Page<Submission> submissions = submissionService.getSubmissionsByUser(pageable);
        return ResponseEntity.ok(submissions.map(this::convertToResponse));
    }

    @GetMapping("/problem/{problemId}")
    public ResponseEntity<Page<SubmissionResponse>> getSubmissionsByProblem(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submitTime").descending());
        Page<Submission> submissions = submissionService.getSubmissionsByProblem(problemId, pageable);
        return ResponseEntity.ok(submissions.map(this::convertToResponse));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<Page<SubmissionResponse>> getSubmissionsByExam(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("submitTime").descending());
        Page<Submission> submissions = submissionService.getSubmissionsByExam(examId, pageable);
        return ResponseEntity.ok(submissions.map(this::convertToResponse));
    }

    @PostMapping("/{id}/resubmit")
    public ResponseEntity<SubmissionResponse> resubmit(@PathVariable Long id) {
        Submission submission = submissionService.resubmit(id);
        return ResponseEntity.ok(convertToResponse(submission));
    }

    private SubmissionResponse convertToResponse(Submission submission) {
        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setProblemId(submission.getProblem().getId());
        response.setProblemTitle(submission.getProblem().getTitle());
        response.setStudentName(submission.getStudent().getUsername());
        response.setStatus(submission.getStatus());
        response.setScore(submission.getScore());
        response.setTimeUsed(submission.getTimeUsed());
        response.setMemoryUsed(submission.getMemoryUsed());
        response.setSubmitTime(submission.getSubmitTime());
        return response;
    }
}