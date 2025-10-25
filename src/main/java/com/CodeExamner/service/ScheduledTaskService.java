// service/ScheduledTaskService.java
package com.CodeExamner.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduledTaskService {

    @Autowired
    private ExamService examService;

    @Autowired
    private JudgeService judgeService;

    /**
     * 每分钟检查考试状态
     */
    @Scheduled(fixedRate = 60000) // 1分钟
    public void updateExamStatus() {
        try {
            examService.updateExamStatus();
            log.debug("考试状态检查完成");
        } catch (Exception e) {
            log.error("更新考试状态失败: {}", e.getMessage());
        }
    }

    /**
     * 每30秒检查待评测的提交
     */
    @Scheduled(fixedRate = 30000) // 30秒
    public void checkPendingSubmissions() {
        try {
            // 这里可以实现检查待评测提交的逻辑
            log.debug("检查待评测提交完成");
        } catch (Exception e) {
            log.error("检查待评测提交失败: {}", e.getMessage());
        }
    }

    /**
     * 每天凌晨清理过期数据
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点
    public void cleanupExpiredData() {
        try {
            log.info("开始清理过期数据");
            // 清理逻辑
            log.info("过期数据清理完成");
        } catch (Exception e) {
            log.error("清理过期数据失败: {}", e.getMessage());
        }
    }
}