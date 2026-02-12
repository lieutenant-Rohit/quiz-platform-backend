package com.college.quizplatform.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiError {

    private Instant timestamp;
    private int status;
    private String message;
}