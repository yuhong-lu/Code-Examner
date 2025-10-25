// judge0/Judge0Client.java
package com.CodeExamner.judge0;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class Judge0Client {

    @Value("${judge0.base-url:http://localhost:2358}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public Judge0Client() {
        this.restTemplate = new RestTemplate();
    }

    public Judge0Submission submitCode(Judge0Submission submission) {
        try {
            String url = baseUrl + "/submissions?base64_encoded=false&wait=false";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Judge0Submission> request = new HttpEntity<>(submission, headers);

            ResponseEntity<Judge0Submission> response = restTemplate.postForEntity(
                    url, request, Judge0Submission.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response.getBody();
            } else {
                log.error("Judge0提交失败: {}", response.getStatusCode());
                throw new RuntimeException("评测服务暂时不可用");
            }
        } catch (Exception e) {
            log.error("调用Judge0失败: {}", e.getMessage());
            throw new RuntimeException("评测服务暂时不可用");
        }
    }

    public Judge0Result getSubmissionResult(String token) {
        try {
            String url = baseUrl + "/submissions/" + token + "?base64_encoded=false";

            ResponseEntity<Judge0Result> response = restTemplate.getForEntity(
                    url, Judge0Result.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                log.error("获取评测结果失败: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("获取评测结果失败: {}", e.getMessage());
            return null;
        }
    }
}