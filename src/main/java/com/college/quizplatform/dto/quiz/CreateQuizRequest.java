package com.college.quizplatform.dto.quiz;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// CreateQuizRequest.java
@Getter @Setter
public class CreateQuizRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer duration;

    private String description;
    private int marksPerQuestion = 1;
    private int negativeMarks = 0;

    // ADD THIS FIELD TO PREVENT MAPPING ERRORS
    private int totalMarks = 0;
}