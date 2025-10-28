// config/Judge0Config.java
package com.CodeExamner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.judge0")
public class Judge0Config {
    private String baseUrl;
    private String callbackUrl;
    private String apiKey = ""; // 如果有 API 密钥

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}