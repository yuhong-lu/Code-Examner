// entity/Submission.java
package com.CodeExamner.entity;

import com.CodeExamner.entity.enums.JudgeStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam; // 如果是考试提交，记录考试ID

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;

    private String language = "java"; // 编程语言

    @Enumerated(EnumType.STRING)
    private JudgeStatus status = JudgeStatus.PENDING;

    private Integer score; // 得分
    private Integer timeUsed; // 时间消耗(ms)
    private Integer memoryUsed; // 内存消耗(KB)

    private LocalDateTime submitTime;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    private List<SubmissionDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        submitTime = LocalDateTime.now();
    }
}