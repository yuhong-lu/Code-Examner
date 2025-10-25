// repository/TestCaseRepository.java
package com.CodeExamner.repository;

import com.CodeExamner.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProblemId(Long problemId);
    List<TestCase> findByProblemIdAndIsSample(Long problemId, Boolean isSample);
}