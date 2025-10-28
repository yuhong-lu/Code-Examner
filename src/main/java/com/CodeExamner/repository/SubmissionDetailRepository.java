// repository/SubmissionDetailRepository.java
package com.CodeExamner.repository;

import com.CodeExamner.entity.SubmissionDetail;
import com.CodeExamner.entity.enums.JudgeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionDetailRepository extends JpaRepository<SubmissionDetail, Long> {
    List<SubmissionDetail> findBySubmissionId(Long submissionId);
    SubmissionDetail findBySubmissionIdAndTestCaseId(Long submissionId, Long testCaseId);

    // 统计相关方法
    Long countBySubmissionId(Long submissionId);
    Long countBySubmissionIdAndStatus(Long submissionId, JudgeStatus status);
}