package com.college.quizplatform.dto.admin;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminResultResponse {

    private int rank;
    private String studentId;
    // FIX: Added fields for frontend display
    private String studentName;
    private String studentEmail;
    private int score;
    private boolean submitted;
}