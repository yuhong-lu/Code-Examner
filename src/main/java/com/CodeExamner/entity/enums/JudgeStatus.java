// entity/enums/JudgeStatus.java
package com.CodeExamner.entity.enums;

public enum JudgeStatus {
    PENDING,           // 等待中
    JUDGING,           // 评测中
    ACCEPTED,          // 通过
    WRONG_ANSWER,      // 答案错误
    TIME_LIMIT_EXCEEDED, // 时间超限
    MEMORY_LIMIT_EXCEEDED, // 内存超限
    RUNTIME_ERROR,     // 运行时错误
    COMPILATION_ERROR, // 编译错误
    SECURITY_ERROR,    // 安全性错误
    NO_TEST_CASES,     // 无测试用例
    SUBMISSION_ERROR,  // 提交错误
    SYSTEM_ERROR       // 系统错误
}