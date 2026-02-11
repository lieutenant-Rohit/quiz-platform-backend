package com.college.quizplatform.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateQuestionRequest {

    @NotBlank
    private String quizId;

    @NotBlank
    private String questionText;

    @NotEmpty
    @Size(min = 2)
    private List<String> options;

    private int correctAnswerIndex;

    private String category;
}