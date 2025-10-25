// service/impl/ExamServiceImpl.java
package com.CodeExamner.service.impl;

import com.CodeExamner.entity.*;
import com.CodeExamner.entity.enums.ExamStatus;
import com.CodeExamner.repository.ExamRepository;
import com.CodeExamner.repository.ExamProblemRepository;
import com.CodeExamner.repository.ProblemRepository;
import com.CodeExamner.service.ExamService;
import com.CodeExamner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamProblemRepository examProblemRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserService userService;

    @Override
    public Exam createExam(Exam exam) {
        User currentUser = userService.getCurrentUser();
        exam.setCreatedBy(currentUser);
        exam.updateStatus(); // 设置初始状态
        return examRepository.save(exam);
    }

    @Override
    public Exam updateExam(Long id, Exam exam) {
        Exam existingExam = getExamById(id);
        checkExamOwnership(existingExam);

        existingExam.setTitle(exam.getTitle());
        existingExam.setDescription(exam.getDescription());
        existingExam.setStartTime(exam.getStartTime());
        existingExam.setEndTime(exam.getEndTime());
        existingExam.setDuration(exam.getDuration());
        existingExam.updateStatus();

        return examRepository.save(existingExam);
    }

    @Override
    public void deleteExam(Long id) {
        Exam exam = getExamById(id);
        checkExamOwnership(exam);
        examRepository.delete(exam);
    }

    @Override
    public Exam getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        // 教师和管理员可以查看任何考试，学生只能查看可访问的考试
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole().name().startsWith("ROLE_STUDENT")) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(exam.getStartTime()) && exam.getStatus() != ExamStatus.SCHEDULED) {
                throw new RuntimeException("考试尚未开始");
            }
        }

        return exam;
    }

    @Override
    public Page<Exam> getExamsByCreator(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        // 需要确保 ExamRepository 中有这个方法
        return examRepository.findByCreatedById(currentUser.getId(), pageable);
    }

    @Override
    public Page<Exam> getAvailableExams(Pageable pageable) {
        User currentUser = userService.getCurrentUser();

        // 学生只能看到已开始或即将开始的考试
        if (currentUser.getRole().name().startsWith("ROLE_STUDENT")) {
            // 需要确保 ExamRepository 中有 findByStatusIn 方法
            return examRepository.findByStatusIn(
                    List.of(ExamStatus.SCHEDULED, ExamStatus.ONGOING), pageable);
        }

        // 教师和管理员看到所有考试
        return examRepository.findAll(pageable);
    }

    // service/impl/ExamServiceImpl.java
// 修复 addProblemToExam 方法
    @Override
    public Exam addProblemToExam(Long examId, Long problemId, Integer score) {
        Exam exam = getExamById(examId);  // 这里使用 examId 而不是 id
        checkExamOwnership(exam);

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));

        ExamProblem examProblem = new ExamProblem();
        examProblem.setExam(exam);
        examProblem.setProblem(problem);
        examProblem.setScore(score);
        examProblem.setSequence(exam.getProblems().size() + 1);

        examProblemRepository.save(examProblem);
        return exam;
    }

    @Override
    public Exam removeProblemFromExam(Long examId, Long problemId) {
        Exam exam = getExamById(examId);
        checkExamOwnership(exam);

        // 需要确保 ExamProblemRepository 中有这个方法
        ExamProblem examProblem = examProblemRepository.findByExamIdAndProblemId(examId, problemId)
                .orElseThrow(() -> new RuntimeException("考试中不存在此题目"));

        examProblemRepository.delete(examProblem);
        return exam;
    }

    @Override
    public List<Exam> getOngoingExams() {
        LocalDateTime now = LocalDateTime.now();
        return examRepository.findOngoingExams(now);
    }

    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void updateExamStatus() {
        List<Exam> exams = examRepository.findAll();
        for (Exam exam : exams) {
            ExamStatus oldStatus = exam.getStatus();
            exam.updateStatus();

            if (oldStatus != exam.getStatus()) {
                examRepository.save(exam);
                // 可以在这里添加状态变更的通知逻辑
            }
        }
    }

    @Override
    public boolean canStudentTakeExam(Long examId, Long studentId) {
        Exam exam = getExamById(examId);
        LocalDateTime now = LocalDateTime.now();

        return exam.getStatus() == ExamStatus.ONGOING &&
                now.isAfter(exam.getStartTime()) &&
                now.isBefore(exam.getEndTime());
    }

    @Override
    public Exam startExamForStudent(Long examId) {
        User currentUser = userService.getCurrentUser();
        if (!canStudentTakeExam(examId, currentUser.getId())) {
            throw new RuntimeException("无法参加此考试");
        }
        return getExamById(examId);
    }

    private void checkExamOwnership(Exam exam) {
        User currentUser = userService.getCurrentUser();
        if (!exam.getCreatedBy().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().name().startsWith("ROLE_ADMIN")) {
            throw new RuntimeException("无权操作此考试");
        }
    }
}