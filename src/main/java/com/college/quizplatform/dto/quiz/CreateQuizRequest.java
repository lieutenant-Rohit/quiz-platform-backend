package com.college.quizplatform.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuizRequest {

    @NotBlank
    private String title;

    private int marksPerQuestion;
    private int negativeMarks;

    private String description;
}