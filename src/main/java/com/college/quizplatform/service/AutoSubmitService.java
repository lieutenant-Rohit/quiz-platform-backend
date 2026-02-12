package com.college.quizplatform.service;

import com.college.quizplatform.model.Attempt;
import com.college.quizplatform.repository.AttemptRepository;
import com.college.quizplatform.repository.QuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoSubmitService {

    private final AttemptRepository attemptRepository;
    private final QuizSessionRepository sessionRepository;
    private final AttemptService attemptService;

    // Runs every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void autoSubmitExpiredAttempts() {

        List<Attempt> attempts = attemptRepository.findAll();

        for (Attempt attempt : attempts) {

            if (!attempt.isSubmitted()) {

                var session = sessionRepository
                        .findById(attempt.getSessionId())
                        .orElse(null);

                if (session != null &&
                        Instant.now().isAfter(session.getScheduledEndTime())) {

                    attemptService.submitAttempt(attempt.getId());
                }
            }
        }
    }
}