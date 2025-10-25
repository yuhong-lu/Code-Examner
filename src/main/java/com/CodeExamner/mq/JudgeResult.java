// mq/JudgeResult.java
package com.CodeExamner.mq;

import com.CodeExamner.entity.enums.JudgeStatus;
import lombok.Data;

@Data
public class JudgeResult {
    private Long submissionId;
    private JudgeStatus status;
    private Integer score;
    private Integer timeUsed;
    private Integer memoryUsed;
    private String message;
}