// dto/response/SubmissionResponse.java
package com.CodeExamner.dto.response;

import com.CodeExamner.entity.enums.JudgeStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmissionResponse {
    private Long id;
    private Long problemId;
    private String problemTitle;
    private String studentName;
    private JudgeStatus status;
    private Integer score;
    private Integer timeUsed;
    private Integer memoryUsed;
    private LocalDateTime submitTime;

    // 便捷方法
    public boolean isAccepted() {
        return status == JudgeStatus.ACCEPTED;
    }

    public String getStatusText() {
        return status != null ? status.toString() : "UNKNOWN";
    }
}