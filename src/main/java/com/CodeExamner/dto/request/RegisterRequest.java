// dto/request/RegisterRequest.java
package com.CodeExamner.dto.request;

import com.CodeExamner.entity.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String email;
    private UserRole role;
    private String studentId;    // 学生注册时需要
    private String realName;     // 学生/教师注册时需要
    private String className;    // 学生注册时需要
}