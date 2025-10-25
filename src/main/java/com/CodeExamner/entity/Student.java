// entity/Student.java
package com.CodeExamner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "students")
public class Student extends User {
    private String studentId; // 学号
    private String realName;
    private String className; // 班级
}