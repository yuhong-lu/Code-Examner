// entity/enums/JudgeStatus.java
package com.CodeExamner.entity.enums;

public enum JudgeStatus {
    PENDING,        // 等待评测
    JUDGING,        // 评测中
    ACCEPTED,       // 通过
    WRONG_ANSWER,   // 答案错误
    TIME_LIMIT_EXCEEDED,    // 时间超限
    MEMORY_LIMIT_EXCEEDED,  // 内存超限
    RUNTIME_ERROR,          // 运行时错误
    COMPILATION_ERROR       // 编译错误
}