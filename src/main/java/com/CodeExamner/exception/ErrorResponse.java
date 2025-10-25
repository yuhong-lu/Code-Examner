// exception/ErrorResponse.java
package com.CodeExamner.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private int code;
    private String message;
    private long timestamp;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}