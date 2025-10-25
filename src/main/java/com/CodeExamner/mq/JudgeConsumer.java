// mq/JudgeConsumer.java
package com.CodeExamner.mq;

import com.CodeExamner.service.JudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JudgeConsumer {

    @Autowired
    private JudgeService judgeService;

    @RabbitListener(queues = "judge.queue")
    public void receiveJudgeRequest(JudgeRequest request) {
        try {
            log.info("接收到评测请求: submissionId={}", request.getSubmissionId());
            // 这里需要根据request创建Submission对象并调用评测服务
            // 简化处理，实际需要从数据库查询submission
        } catch (Exception e) {
            log.error("处理评测请求失败: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "result.queue")
    public void receiveJudgeResult(JudgeResult result) {
        try {
            log.info("接收到评测结果: submissionId={}, status={}", result.getSubmissionId(), result.getStatus());
            // 更新数据库中的提交状态
        } catch (Exception e) {
            log.error("处理评测结果失败: {}", e.getMessage());
        }
    }
}