// dto/request/SubmissionRequest.java
package com.CodeExamner.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class SubmissionRequest {
    @NotNull
    private Long problemId;

    private Long examId;

    @NotNull
    private String code;

    private String language = "java";
}