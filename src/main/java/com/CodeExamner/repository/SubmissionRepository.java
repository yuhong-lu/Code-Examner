// repository/SubmissionRepository.java
package com.CodeExamner.repository;

import com.CodeExamner.entity.Submission;
import com.CodeExamner.entity.enums.JudgeStatus; // 需要导入JudgeStatus
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // 需要导入LocalDateTime
import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // 分页查询方法
    Page<Submission> findByStudentId(Long studentId, Pageable pageable);
    Page<Submission> findByProblemId(Long problemId, Pageable pageable);
    Page<Submission> findByProblemIdAndStudentId(Long problemId, Long studentId, Pageable pageable);

    // 考试相关查询
    List<Submission> findByExamIdAndStudentId(Long examId, Long studentId);

    @Query("SELECT s FROM Submission s WHERE s.exam.id = :examId")
    Page<Submission> findByExamId(Long examId, Pageable pageable);

    @Query("SELECT s FROM Submission s WHERE s.exam.id = :examId AND s.student.id = :studentId")
    Page<Submission> findByExamIdAndStudentId(Long examId, Long studentId, Pageable pageable);

    // ============== 为StatisticsService添加的统计方法 ==============

    // 按状态统计
    Long countByStatus(JudgeStatus status);

    // 按学生统计
    Long countByStudentId(Long studentId);
    Long countByStudentIdAndStatus(Long studentId, JudgeStatus status);
    Long countByStudentIdAndSubmitTimeAfter(Long studentId, LocalDateTime submitTime);

    // 按题目统计
    Long countByProblemId(Long problemId);
    Long countByProblemIdAndStatus(Long problemId, JudgeStatus status);

    // 还可以添加一些有用的统计方法
    Long countByExamId(Long examId);
    Long countByExamIdAndStatus(Long examId, JudgeStatus status);
}