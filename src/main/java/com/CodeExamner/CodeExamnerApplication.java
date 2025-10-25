// CodeExamnerApplication.java
package com.CodeExamner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 启用定时任务，用于更新考试状态
public class CodeExamnerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeExamnerApplication.class, args);
    }
}