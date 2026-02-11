package com.college.quizplatform.controller.student;

import com.college.quizplatform.dto.attempt.StartAttemptResponse;
import com.college.quizplatform.dto.attempt.SubmitAnswerRequest;
import com.college.quizplatform.model.Attempt;
import com.college.quizplatform.service.AttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/attempts")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/start/{sessionId}")
    public StartAttemptResponse startAttempt(
            @PathVariable String sessionId) {

        return attemptService.startAttempt(sessionId);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/answer")
    public void submitAnswer(
            @Valid @RequestBody SubmitAnswerRequest request) {

        attemptService.submitAnswer(request);
    }
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/submit/{attemptId}")
    public int submitAttempt(@PathVariable String attemptId) {
        return attemptService.submitAttempt(attemptId);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/leaderboard/{sessionId}")
    public List<Attempt> leaderboard(@PathVariable String sessionId) {
        return attemptService.getLeaderboard(sessionId);
    }
}