// service/UserService.java
package com.CodeExamner.service;

import com.CodeExamner.entity.User;
import com.CodeExamner.entity.enums.UserRole;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User register(User user);
    String login(String username, String password);
    User getCurrentUser();
    User findById(Long id);
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    void changePassword(String oldPassword, String newPassword);
}