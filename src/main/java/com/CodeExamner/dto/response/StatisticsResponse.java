// dto/response/StatisticsResponse.java
package com.CodeExamner.dto.response;

import lombok.Data;

@Data
public class StatisticsResponse {
    private Long totalUsers;
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalProblems;
    private Long publicProblems;
    private Long totalExams;
    private Long ongoingExams;
    private Long totalSubmissions;
    private Long acceptedSubmissions;

    // 可以添加计算属性
    public Double getAcceptanceRate() {
        if (totalSubmissions == null || totalSubmissions == 0) {
            return 0.0;
        }
        return (double) acceptedSubmissions / totalSubmissions * 100;
    }
}