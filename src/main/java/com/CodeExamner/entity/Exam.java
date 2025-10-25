// entity/Exam.java
package com.CodeExamner.entity;

import com.CodeExamner.entity.enums.ExamStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ExamStatus status = ExamStatus.DRAFT;

    private Integer duration; // 考试时长(分钟)

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<ExamProblem> problems = new ArrayList<>();

    @PrePersist
    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            status = ExamStatus.SCHEDULED;
        } else if (now.isAfter(startTime) && now.isBefore(endTime)) {
            status = ExamStatus.ONGOING;
        } else if (now.isAfter(endTime)) {
            status = ExamStatus.FINISHED;
        }
    }
}