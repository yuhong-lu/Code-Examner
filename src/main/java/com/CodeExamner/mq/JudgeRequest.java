// mq/JudgeRequest.java
package com.CodeExamner.mq;

import lombok.Data;

@Data
public class JudgeRequest {
    private Long submissionId;
    private String code;
    private Long problemId;
    private String language;
}
