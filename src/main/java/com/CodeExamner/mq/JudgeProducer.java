// mq/JudgeProducer.java
package com.CodeExamner.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JudgeProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendJudgeRequest(JudgeRequest request) {
        try {
            rabbitTemplate.convertAndSend("judge.exchange", "judge.routing.key", request);
            log.info("发送评测请求: submissionId={}", request.getSubmissionId());
        } catch (Exception e) {
            log.error("发送评测请求失败: {}", e.getMessage());
            throw new RuntimeException("消息队列服务暂时不可用");
        }
    }

    public void sendJudgeResult(JudgeResult result) {
        try {
            rabbitTemplate.convertAndSend("result.exchange", "result.routing.key", result);
            log.info("发送评测结果: submissionId={}, status={}", result.getSubmissionId(), result.getStatus());
        } catch (Exception e) {
            log.error("发送评测结果失败: {}", e.getMessage());
        }
    }
}