// entity/SubmissionDetail.java
package com.CodeExamner.entity;

import com.CodeExamner.entity.enums.JudgeStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "submission_details")
public class SubmissionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "test_case_id", nullable = false)
    private TestCase testCase;

    @Enumerated(EnumType.STRING)
    private JudgeStatus status;

    private Integer timeUsed;
    private Integer memoryUsed;

    @Column(columnDefinition = "TEXT")
    private String output;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}