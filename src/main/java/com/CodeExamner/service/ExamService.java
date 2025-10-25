// service/ExamService.java
package com.CodeExamner.service;

import com.CodeExamner.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamService {
    Exam createExam(Exam exam);
    Exam updateExam(Long id, Exam exam);
    void deleteExam(Long id);
    Exam getExamById(Long id);
    Page<Exam> getExamsByCreator(Pageable pageable);
    Page<Exam> getAvailableExams(Pageable pageable);
    Exam addProblemToExam(Long examId, Long problemId, Integer score);
    Exam removeProblemFromExam(Long examId, Long problemId);
    List<Exam> getOngoingExams();
    void updateExamStatus();
    boolean canStudentTakeExam(Long examId, Long studentId);
    Exam startExamForStudent(Long examId);
}