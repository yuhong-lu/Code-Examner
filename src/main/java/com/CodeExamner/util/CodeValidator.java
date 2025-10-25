// util/CodeValidator.java
package com.CodeExamner.util;

import org.springframework.stereotype.Component;

@Component
public class CodeValidator {

    public boolean validateJavaCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        // 基础Java代码验证
        if (!code.contains("class")) {
            return false;
        }

        // 检查代码长度
        if (code.length() > 10000) {
            return false;
        }

        // 检查危险代码（简单版本）
        String[] dangerousPatterns = {
                "Runtime.getRuntime()",
                "ProcessBuilder",
                "System.exit",
                "java.lang.reflect",
                "java.io.File",
                "java.net."
        };

        for (String pattern : dangerousPatterns) {
            if (code.contains(pattern)) {
                return false;
            }
        }

        return true;
    }
}