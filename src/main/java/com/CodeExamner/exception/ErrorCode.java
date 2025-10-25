// exception/ErrorCode.java
package com.CodeExamner.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 用户相关
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    EMAIL_EXISTS(1003, "邮箱已被注册"),
    INVALID_CREDENTIALS(1004, "用户名或密码错误"),

    // 题目相关
    PROBLEM_NOT_FOUND(2001, "题目不存在"),
    PROBLEM_ACCESS_DENIED(2002, "无权访问此题目"),

    // 考试相关
    EXAM_NOT_FOUND(3001, "考试不存在"),
    EXAM_ACCESS_DENIED(3002, "无权访问此考试"),
    EXAM_NOT_STARTED(3003, "考试尚未开始"),
    EXAM_ENDED(3004, "考试已结束"),

    // 提交相关
    SUBMISSION_NOT_FOUND(4001, "提交记录不存在"),
    SUBMISSION_ACCESS_DENIED(4002, "无权查看此提交记录"),

    // 系统错误
    INTERNAL_ERROR(5001, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}