// controller/JudgeCallbackController.java
package com.CodeExamner.controller;

import com.CodeExamner.dto.request.Judge0CallbackRequest;
import com.CodeExamner.service.JudgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@Slf4j
@RestController
@RequestMapping("/judge/callback")
@RequiredArgsConstructor
public class JudgeCallbackController {

    private final JudgeService judgeService;

    @PostMapping
    public ResponseEntity<String> handleCallback(@RequestBody Judge0CallbackRequest request) {
        try {
            log.info("收到 Judge0 回调: token={}, status={}",
                    request.getToken(), request.getStatus().getDescription());

            // 这里需要根据 token 找到对应的 submissionId
            // 简化处理：假设 token 就是 submissionId
            Long submissionId = Long.parseLong(request.getToken());

            judgeService.processJudgeResult(submissionId, request.getToken());

            return ResponseEntity.ok("Callback processed successfully");
        } catch (Exception e) {
            log.error("处理回调失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error processing callback");
        }
    }
}