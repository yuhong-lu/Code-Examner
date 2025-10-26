// repository/UserRepository.java
package com.CodeExamner.repository;

import com.CodeExamner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // 新增统计方法
    long countByRole(String role);

    // 或者使用 @Query 更精确
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = ?1")
    long countByRole(com.CodeExamner.entity.enums.UserRole role);
}