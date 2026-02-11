package com.college.quizplatform.dto.quiz;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuestionResponse {

    private String id;
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex; // Admin can see
    private String category;
}