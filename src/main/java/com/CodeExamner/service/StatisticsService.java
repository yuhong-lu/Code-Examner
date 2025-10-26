// service/StatisticsService.java
package com.CodeExamner.service;

import com.CodeExamner.dto.response.StatisticsResponse;
import com.CodeExamner.entity.enums.JudgeStatus;
import com.CodeExamner.judge0.Judge0Status;
import com.CodeExamner.repository.ExamRepository;
import com.CodeExamner.repository.ProblemRepository;
import com.CodeExamner.repository.SubmissionRepository;
import com.CodeExamner.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.CodeExamner.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    public StatisticsResponse getSystemStatistics() {
        StatisticsResponse response = new StatisticsResponse();

        // 用户统计
        response.setTotalUsers(userRepository.count());
        response.setTotalStudents(userRepository.countByRole(UserRole.STUDENT));
        response.setTotalTeachers(userRepository.countByRole(UserRole.TEACHER));

        // 题目统计
        response.setTotalProblems(problemRepository.count());
        response.setPublicProblems(problemRepository.countByIsPublicTrue());

        // 考试统计
        response.setTotalExams(examRepository.count());
        response.setOngoingExams(examRepository.countByStatus("ONGOING"));

        // 提交统计
        response.setTotalSubmissions(submissionRepository.count());
        response.setAcceptedSubmissions(submissionRepository.countByStatus(JudgeStatus.ACCEPTED));

        return response;
    }

    public Map<String, Object> getUserStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 用户提交统计
        Long totalSubmissions = submissionRepository.countByStudentId(userId);
        Long acceptedSubmissions = submissionRepository.countByStudentIdAndStatus(userId, JudgeStatus.ACCEPTED);

        stats.put("totalSubmissions", totalSubmissions);
        stats.put("acceptedSubmissions", acceptedSubmissions);
        stats.put("acceptanceRate", totalSubmissions > 0 ?
                (double) acceptedSubmissions / totalSubmissions * 100 : 0);

        // 最近活动
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        Long recentSubmissions = submissionRepository.countByStudentIdAndSubmitTimeAfter(userId, oneWeekAgo);
        stats.put("recentSubmissions", recentSubmissions);

        return stats;
    }

    public Map<String, Object> getProblemStatistics(Long problemId) {
        Map<String, Object> stats = new HashMap<>();

        Long totalSubmissions = submissionRepository.countByProblemId(problemId);
        Long acceptedSubmissions = submissionRepository.countByProblemIdAndStatus(problemId, JudgeStatus.ACCEPTED);

        stats.put("totalSubmissions", totalSubmissions);
        stats.put("acceptedSubmissions", acceptedSubmissions);
        stats.put("acceptanceRate", totalSubmissions > 0 ?
                (double) acceptedSubmissions / totalSubmissions * 100 : 0);

        return stats;
    }
}
