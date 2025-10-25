// service/EmailService.java
package com.CodeExamner.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("邮件发送成功: {} -> {}", subject, to);
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage());
        }
    }

    @Async
    public void sendHtmlMessage(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML邮件发送成功: {} -> {}", subject, to);
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败: {}", e.getMessage());
        }
    }

    // 发送评测结果通知
    @Async
    public void sendJudgeResultNotification(String to, String problemTitle, String status) {
        String subject = "代码评测结果 - " + problemTitle;
        String text = String.format("""
            您的代码提交评测已完成！
            
            题目：%s
            状态：%s
            
            请登录系统查看详细结果。
            """, problemTitle, status);

        sendSimpleMessage(to, subject, text);
    }

    // 发送考试开始通知
    @Async
    public void sendExamStartNotification(String to, String examTitle, String startTime) {
        String subject = "考试提醒 - " + examTitle;
        String htmlContent = String.format("""
            <html>
            <body>
                <h2>考试提醒</h2>
                <p>您参加的考试即将开始：</p>
                <ul>
                    <li><strong>考试名称：</strong>%s</li>
                    <li><strong>开始时间：</strong>%s</li>
                </ul>
                <p>请提前做好准备！</p>
            </body>
            </html>
            """, examTitle, startTime);

        sendHtmlMessage(to, subject, htmlContent);
    }
}