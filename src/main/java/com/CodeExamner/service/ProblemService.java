// service/ProblemService.java
package com.CodeExamner.service;

import com.CodeExamner.entity.Problem;
import com.CodeExamner.entity.TestCase;
import com.CodeExamner.entity.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemService {
    Problem createProblem(Problem problem);
    Problem updateProblem(Long id, Problem problem);
    void deleteProblem(Long id);
    Problem getProblemById(Long id);
    Page<Problem> getPublicProblems(Pageable pageable);
    Page<Problem> getAccessibleProblems(Pageable pageable);
    Page<Problem> getProblemsByCreator(Pageable pageable);
    Problem addTestCase(Long problemId, TestCase testCase);
    List<TestCase> getTestCases(Long problemId);
    List<TestCase> getSampleTestCases(Long problemId);
    Page<Problem> searchProblems(String keyword, Difficulty difficulty, Pageable pageable);
}