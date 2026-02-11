package com.college.quizplatform.dto.attempt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StartAttemptResponse {

    private String attemptId;
    private String sessionId;
    private String quizId;
}