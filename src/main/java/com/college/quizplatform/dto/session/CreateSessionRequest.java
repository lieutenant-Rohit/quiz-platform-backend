package com.college.quizplatform.dto.session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
public class CreateSessionRequest {

    @NotBlank
    private String quizId;

    @NotBlank
    private String sessionName;

    @NotNull
    private Instant scheduledStartTime;

    @NotNull
    private Instant scheduledEndTime;

    private Set<String> allowedStudentIds;
}