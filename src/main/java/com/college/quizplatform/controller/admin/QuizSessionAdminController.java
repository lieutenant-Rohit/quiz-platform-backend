package com.college.quizplatform.controller.admin;

import com.college.quizplatform.dto.session.CreateSessionRequest;
import com.college.quizplatform.model.QuizSession;
import com.college.quizplatform.service.QuizSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sessions")
@RequiredArgsConstructor
public class QuizSessionAdminController {

    private final QuizSessionService sessionService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
    @PostMapping
    public QuizSession createSession(
            @Valid @RequestBody CreateSessionRequest request) {

        return sessionService.createSession(request);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
    @PostMapping("/start/{sessionId}")
    public QuizSession startSession(@PathVariable String sessionId) {
        return sessionService.startSession(sessionId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ADMIN')")
    @PostMapping("/end/{sessionId}")
    public QuizSession endSession(@PathVariable String sessionId) {
        return sessionService.endSession(sessionId);
    }
}