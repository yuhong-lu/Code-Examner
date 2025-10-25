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
}