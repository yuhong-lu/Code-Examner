// repository/SubmissionRepository.java
package com.CodeExamner.repository;

import com.CodeExamner.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Page<Submission> findByStudentId(Long studentId, Pageable pageable);
    Page<Submission> findByProblemId(Long problemId, Pageable pageable);

    // 添加这两个缺失的方法
    Page<Submission> findByProblemIdAndStudentId(Long problemId, Long studentId, Pageable pageable);
    List<Submission> findByExamIdAndStudentId(Long examId, Long studentId);

    @Query("SELECT s FROM Submission s WHERE s.exam.id = :examId")
    Page<Submission> findByExamId(Long examId, Pageable pageable);

    // 添加这个方法用于考试提交查询
    @Query("SELECT s FROM Submission s WHERE s.exam.id = :examId AND s.student.id = :studentId")
    Page<Submission> findByExamIdAndStudentId(Long examId, Long studentId, Pageable pageable);
}