// dto/response/AuthResponse.java
package com.CodeExamner.dto.response;

import com.CodeExamner.entity.enums.UserRole;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private UserRole role;
    private String message;

    public AuthResponse(String token, Long userId, String username, UserRole role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public AuthResponse(String message) {
        this.message = message;
    }
}