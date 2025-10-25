// controller/ProblemController.java
package com.CodeExamner.controller;

import com.CodeExamner.dto.request.ProblemCreateRequest;
import com.CodeExamner.dto.response.ProblemResponse;
import com.CodeExamner.entity.Problem;
import com.CodeExamner.entity.TestCase;
import com.CodeExamner.entity.enums.Difficulty;
import com.CodeExamner.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problems")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ProblemResponse> createProblem(@Valid @RequestBody ProblemCreateRequest request) {
        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setTemplateCode(request.getTemplateCode());
        problem.setDifficulty(request.getDifficulty());
        problem.setTimeLimit(request.getTimeLimit());
        problem.setMemoryLimit(request.getMemoryLimit());
        problem.setIsPublic(request.getIsPublic());

        Problem created = problemService.createProblem(problem);
        return ResponseEntity.ok(convertToResponse(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ProblemResponse> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody ProblemCreateRequest request) {
        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setTemplateCode(request.getTemplateCode());
        problem.setDifficulty(request.getDifficulty());
        problem.setTimeLimit(request.getTimeLimit());
        problem.setMemoryLimit(request.getMemoryLimit());
        problem.setIsPublic(request.getIsPublic());

        Problem updated = problemService.updateProblem(id, problem);
        return ResponseEntity.ok(convertToResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getProblem(@PathVariable Long id) {
        Problem problem = problemService.getProblemById(id);
        return ResponseEntity.ok(convertToResponse(problem));
    }

    @GetMapping("/public")
    public ResponseEntity<Page<ProblemResponse>> getPublicProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Problem> problems = problemService.getPublicProblems(pageable);
        return ResponseEntity.ok(problems.map(this::convertToResponse));
    }

    @GetMapping
    public ResponseEntity<Page<ProblemResponse>> getAccessibleProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Problem> problems = problemService.getAccessibleProblems(pageable);
        return ResponseEntity.ok(problems.map(this::convertToResponse));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ProblemResponse>> getMyProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Problem> problems = problemService.getProblemsByCreator(pageable);
        return ResponseEntity.ok(problems.map(this::convertToResponse));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProblemResponse>> searchProblems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Problem> problems = problemService.searchProblems(keyword, difficulty, pageable);
        return ResponseEntity.ok(problems.map(this::convertToResponse));
    }

    @PostMapping("/{id}/test-cases")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ProblemResponse> addTestCase(
            @PathVariable Long id,
            @Valid @RequestBody TestCase testCase) {
        Problem problem = problemService.addTestCase(id, testCase);
        return ResponseEntity.ok(convertToResponse(problem));
    }

    @GetMapping("/{id}/test-cases")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<TestCase>> getTestCases(@PathVariable Long id) {
        List<TestCase> testCases = problemService.getTestCases(id);
        return ResponseEntity.ok(testCases);
    }

    @GetMapping("/{id}/sample-test-cases")
    public ResponseEntity<List<TestCase>> getSampleTestCases(@PathVariable Long id) {
        List<TestCase> testCases = problemService.getSampleTestCases(id);
        return ResponseEntity.ok(testCases);
    }

    private ProblemResponse convertToResponse(Problem problem) {
        ProblemResponse response = new ProblemResponse();
        response.setId(problem.getId());
        response.setTitle(problem.getTitle());
        response.setDescription(problem.getDescription());
        response.setDifficulty(problem.getDifficulty());
        response.setTimeLimit(problem.getTimeLimit());
        response.setMemoryLimit(problem.getMemoryLimit());
        response.setCreatorName(problem.getCreatedBy().getUsername());
        response.setCreateTime(problem.getCreateTime());
        response.setIsPublic(problem.getIsPublic());
        return response;
    }
}