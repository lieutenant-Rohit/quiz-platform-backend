package com.college.quizplatform.controller.student;

import com.college.quizplatform.dto.attempt.StartAttemptResponse;
import com.college.quizplatform.dto.attempt.SubmitAnswerRequest;
import com.college.quizplatform.service.AttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;

    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'STUDENT')")
    @PostMapping("/start/{sessionId}")
    public StartAttemptResponse startAttempt(
            @PathVariable String sessionId) {

        return attemptService.startAttempt(sessionId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'STUDENT')")
    @PostMapping("/answer")
    public void submitAnswer(
            @Valid @RequestBody SubmitAnswerRequest request) {

        attemptService.submitAnswer(request);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'STUDENT')")
    @PostMapping("/submit/{attemptId}")
    public int submitAttempt(@PathVariable String attemptId) {
        return attemptService.submitAttempt(attemptId);
    }
}