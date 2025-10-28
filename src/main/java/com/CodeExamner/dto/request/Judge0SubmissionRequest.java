// dto/Judge0SubmissionRequest.java
package com.CodeExamner.dto.request;

import lombok.Data;

@Data
public class Judge0SubmissionRequest {
    private String source_code;
    private String language_id;
    private String stdin;
    private String expected_output;
    private Integer cpu_time_limit;
    private Integer memory_limit;
    private String callback_url;

    // 构造函数
    public Judge0SubmissionRequest(String code, Integer languageId, String input,
                                   String expectedOutput, String callbackUrl) {
        this.source_code = code;
        this.language_id = languageId.toString();
        this.stdin = input;
        this.expected_output = expectedOutput;
        this.cpu_time_limit = 2; // 2秒限制
        this.memory_limit = 128000; // 128MB
        this.callback_url = callbackUrl;
    }
}