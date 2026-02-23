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

    // Admin uses this to verify correct answers in the dashboard
    private int correctAnswerIndex;

    private String category;
}