// repository/ProblemRepository.java
package com.CodeExamner.repository;

import com.CodeExamner.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;  // 新增导入
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long>, JpaSpecificationExecutor<Problem> {  // 新增继承
    Page<Problem> findByIsPublicTrue(Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE p.createdBy.id = :userId OR p.isPublic = true")
    Page<Problem> findAccessibleProblems(Long userId, Pageable pageable);

    Page<Problem> findByCreatedById(Long userId, Pageable pageable);
}