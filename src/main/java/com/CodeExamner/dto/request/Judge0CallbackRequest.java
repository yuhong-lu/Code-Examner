// dto/Judge0CallbackRequest.java
package com.CodeExamner.dto.request;

import lombok.Data;

@Data
public class Judge0CallbackRequest {
    private String token;
    private Judge0Result status;
    private String stdout;
    private String stderr;
    private String compile_output;
    private String message;
    private Long time;
    private Long memory;

    @Data
    public static class Judge0Result {
        private Integer id;
        private String description;
    }
}