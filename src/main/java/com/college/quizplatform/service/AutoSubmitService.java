package com.college.quizplatform.service;

import com.college.quizplatform.model.Attempt;
import com.college.quizplatform.model.QuizSession;
import com.college.quizplatform.repository.AttemptRepository;
import com.college.quizplatform.repository.QuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutoSubmitService {

    private final AttemptRepository attemptRepository;
    private final QuizSessionRepository sessionRepository;
    private final AttemptService attemptService;

    // Runs every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void autoSubmitExpiredAttempts() {

        List<Attempt> attempts = attemptRepository.findBySubmittedFalse();
        if (attempts.isEmpty()) return;

        // 1. Extract unique session IDs
        Set<String> sessionIds = attempts.stream()
                .map(Attempt::getSessionId)
                .collect(Collectors.toSet());

        // 2. Fetch all sessions in one query
        Map<String, QuizSession> sessionMap = sessionRepository.findAllById(sessionIds).stream()
                .collect(Collectors.toMap(QuizSession::getId, session -> session));

        Instant now = Instant.now();

        for (Attempt attempt : attempts) {
            QuizSession session = sessionMap.get(attempt.getSessionId());

            if (session != null && now.isAfter(session.getScheduledEndTime())) {
                attemptService.submitAttempt(attempt.getId());
            }
        }
    }
}