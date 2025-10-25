// controller/StatisticsController.java
package com.CodeExamner.controller;

import com.CodeExamner.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStatistics(@PathVariable Long userId) {
        Map<String, Object> stats = statisticsService.getUserStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/problem/{problemId}")
    public ResponseEntity<Map<String, Object>> getProblemStatistics(@PathVariable Long problemId) {
        Map<String, Object> stats = statisticsService.getProblemStatistics(problemId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        Map<String, Object> overview = Map.of(
                "status", "running",
                "version", "1.0.0",
                "uptime", "24小时",
                "activeUsers", 150
        );
        return ResponseEntity.ok(overview);
    }
}