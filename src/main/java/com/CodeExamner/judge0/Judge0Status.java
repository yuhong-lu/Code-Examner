// judge0/Judge0Status.java
package com.CodeExamner.judge0;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Judge0Status {
    private Integer id;

    @JsonProperty("description")
    private String description;
}