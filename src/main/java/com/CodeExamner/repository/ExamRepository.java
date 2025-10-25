// repository/ExamRepository.java
package com.CodeExamner.repository;

import com.CodeExamner.entity.Exam;
import com.CodeExamner.entity.enums.ExamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // 保留：按状态查询（不分页）
    List<Exam> findByStatus(ExamStatus status);

    // 保留：按状态列表查询（不分页）
    List<Exam> findByStatusIn(List<ExamStatus> statuses);

    // 保留：按创建者查询（不分页）
    List<Exam> findByCreatedById(Long teacherId);

    // 保留：查询进行中的考试
    @Query("SELECT e FROM Exam e WHERE e.status = 'ONGOING' AND :currentTime BETWEEN e.startTime AND e.endTime")
    List<Exam> findOngoingExams(LocalDateTime currentTime);

    // 新增：按创建者查询（分页版本）
    Page<Exam> findByCreatedById(Long teacherId, Pageable pageable);

    // 新增：按状态列表查询（分页版本）
    Page<Exam> findByStatusIn(List<ExamStatus> statuses, Pageable pageable);
}