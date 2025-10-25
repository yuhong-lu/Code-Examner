// controller/HealthController.java
package com.CodeExamner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApplicationAvailability applicationAvailability;

    @GetMapping
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();

        // 应用状态
        health.put("status", "UP");
        health.put("livenessState", applicationAvailability.getLivenessState());
        health.put("readinessState", applicationAvailability.getReadinessState());

        // 数据库连接检查
        try (Connection connection = dataSource.getConnection()) {
            health.put("database", "UP");
            jdbcTemplate.execute("SELECT 1");
        } catch (Exception e) {
            health.put("database", "DOWN");
            health.put("databaseError", e.getMessage());
        }

        // 系统信息
        health.put("timestamp", System.currentTimeMillis());
        health.put("version", "1.0.0");

        return health;
    }

    @PostMapping("/liveness/{state}")
    public String changeLivenessState(@PathVariable String state) {
        switch (state.toUpperCase()) {
            case "CORRECT":
                AvailabilityChangeEvent.publish(applicationContext, LivenessState.CORRECT);
                return "Liveness state changed to CORRECT";
            case "BROKEN":
                AvailabilityChangeEvent.publish(applicationContext, LivenessState.BROKEN);
                return "Liveness state changed to BROKEN";
            default:
                return "Invalid state";
        }
    }

    @GetMapping("/readiness")
    public Map<String, String> readinessCheck() {
        Map<String, String> readiness = new HashMap<>();
        readiness.put("status", "READY");
        readiness.put("message", "Application is ready to receive traffic");
        return readiness;
    }
}