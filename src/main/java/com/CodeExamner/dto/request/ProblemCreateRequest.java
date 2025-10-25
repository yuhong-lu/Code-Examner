// dto/request/ProblemCreateRequest.java
package com.CodeExamner.dto.request;

import com.CodeExamner.entity.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProblemCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String templateCode;

    @NotNull
    private Difficulty difficulty;

    @NotNull
    private Integer timeLimit;

    @NotNull
    private Integer memoryLimit;

    private Boolean isPublic = false;
}