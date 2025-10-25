// service/impl/ProblemServiceImpl.java
package com.CodeExamner.service.impl;

import com.CodeExamner.entity.Problem;
import com.CodeExamner.entity.TestCase;
import com.CodeExamner.entity.User;
import com.CodeExamner.entity.enums.Difficulty;
import com.CodeExamner.repository.ProblemRepository;
import com.CodeExamner.repository.TestCaseRepository;
import com.CodeExamner.service.ProblemService;
import com.CodeExamner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private UserService userService;

    @Override
    public Problem createProblem(Problem problem) {
        User currentUser = userService.getCurrentUser();
        problem.setCreatedBy(currentUser);
        return problemRepository.save(problem);
    }

    @Override
    public Problem updateProblem(Long id, Problem problem) {
        Problem existingProblem = getProblemById(id);
        checkProblemOwnership(existingProblem);

        existingProblem.setTitle(problem.getTitle());
        existingProblem.setDescription(problem.getDescription());
        existingProblem.setTemplateCode(problem.getTemplateCode());
        existingProblem.setDifficulty(problem.getDifficulty());
        existingProblem.setTimeLimit(problem.getTimeLimit());
        existingProblem.setMemoryLimit(problem.getMemoryLimit());
        existingProblem.setIsPublic(problem.getIsPublic());

        return problemRepository.save(existingProblem);
    }

    @Override
    public void deleteProblem(Long id) {
        Problem problem = getProblemById(id);
        checkProblemOwnership(problem);
        problemRepository.delete(problem);
    }

    @Override
    public Problem getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("题目不存在"));

        // 检查访问权限
        User currentUser = userService.getCurrentUser();
        if (!problem.getIsPublic() &&
                !problem.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("无权访问此题目");
        }

        return problem;
    }

    @Override
    public Page<Problem> getPublicProblems(Pageable pageable) {
        return problemRepository.findByIsPublicTrue(pageable);
    }

    @Override
    public Page<Problem> getAccessibleProblems(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return problemRepository.findAccessibleProblems(currentUser.getId(), pageable);
    }

    @Override
    public Page<Problem> getProblemsByCreator(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return problemRepository.findByCreatedById(currentUser.getId(), pageable);
    }

    @Override
    public Problem addTestCase(Long problemId, TestCase testCase) {
        Problem problem = getProblemById(problemId);
        checkProblemOwnership(problem);

        testCase.setProblem(problem);
        testCaseRepository.save(testCase);

        return problem;
    }

    @Override
    public List<TestCase> getTestCases(Long problemId) {
        Problem problem = getProblemById(problemId);
        checkProblemOwnership(problem);

        return testCaseRepository.findByProblemId(problemId);
    }

    @Override
    public List<TestCase> getSampleTestCases(Long problemId) {
        Problem problem = getProblemById(problemId);
        return testCaseRepository.findByProblemIdAndIsSample(problemId, true);
    }

    @Override
    public Page<Problem> searchProblems(String keyword, Difficulty difficulty, Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        Specification<Problem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 只能访问公开题目或自己创建的题目
            Predicate isPublic = cb.isTrue(root.get("isPublic"));
            Predicate isOwner = cb.equal(root.get("createdBy").get("id"), currentUser.getId());
            predicates.add(cb.or(isPublic, isOwner));

            if (keyword != null && !keyword.trim().isEmpty()) {
                Predicate titleLike = cb.like(root.get("title"), "%" + keyword + "%");
                Predicate descLike = cb.like(root.get("description"), "%" + keyword + "%");
                predicates.add(cb.or(titleLike, descLike));
            }

            if (difficulty != null) {
                predicates.add(cb.equal(root.get("difficulty"), difficulty));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return problemRepository.findAll(spec, pageable);
    }

    private void checkProblemOwnership(Problem problem) {
        User currentUser = userService.getCurrentUser();
        if (!problem.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("无权操作此题目");
        }
    }
}