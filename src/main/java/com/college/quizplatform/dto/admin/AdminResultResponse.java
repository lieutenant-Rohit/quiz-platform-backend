package com.college.quizplatform.dto.admin;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminResultResponse {

    private int rank;
    private String studentId;
    private int score;
    private boolean submitted;
}