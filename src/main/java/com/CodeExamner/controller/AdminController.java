// controller/AdminController.java
package com.CodeExamner.controller;

import com.CodeExamner.dto.response.StatisticsResponse;
import com.CodeExamner.entity.User;
import com.CodeExamner.entity.enums.UserRole;  // 正确的导入路径
import com.CodeExamner.service.StatisticsService;
import com.CodeExamner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getSystemStatistics() {
        StatisticsResponse statistics = statisticsService.getSystemStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    // 添加更新用户角色的接口
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long userId,
            @RequestParam UserRole role) {
        User updatedUser = userService.updateUserRole(userId, role);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        StatisticsResponse stats = statisticsService.getSystemStatistics();

        Map<String, Object> dashboard = Map.of(
                "systemStats", stats,
                "recentActivity", "最近活动数据",
                "systemHealth", "系统运行正常"
        );

        return ResponseEntity.ok(dashboard);
    }
}