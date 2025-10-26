// service/UserService.java
package com.CodeExamner.service;

import com.CodeExamner.entity.User;
import com.CodeExamner.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    // 基础用户方法
    User register(User user);
    String login(String username, String password);
    User getCurrentUser();
    User findById(Long id);
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void changePassword(String oldPassword, String newPassword);

    // 新增管理员方法
    Page<User> getAllUsers(Pageable pageable);
    void deleteUser(Long userId);
    User updateUserRole(Long userId, UserRole role);
}