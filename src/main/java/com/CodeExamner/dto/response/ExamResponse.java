// dto/response/ExamResponse.java
package com.CodeExamner.dto.response;

import com.CodeExamner.entity.enums.ExamStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private String creatorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ExamStatus status;
    private Integer duration;
    private Integer problemCount;
    private Long remainingTime; // 剩余时间（分钟）
}