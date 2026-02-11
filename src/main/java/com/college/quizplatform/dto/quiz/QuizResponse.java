package com.college.quizplatform.dto.quiz;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class QuizResponse {

    private String id;
    private String title;
    private String description;
    private boolean active;
    private Instant createdAt;

    private List<QuestionResponse> questions;
}