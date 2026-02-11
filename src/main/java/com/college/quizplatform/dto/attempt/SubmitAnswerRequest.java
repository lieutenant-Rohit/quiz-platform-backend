package com.college.quizplatform.dto.attempt;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerRequest {

    @NotBlank
    private String attemptId;

    @NotBlank
    private String questionId;

    private int selectedOptionIndex;
}