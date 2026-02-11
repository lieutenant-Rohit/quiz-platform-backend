package com.college.quizplatform.service;

import com.college.quizplatform.dto.session.CreateSessionRequest;
import com.college.quizplatform.model.Quiz;
import com.college.quizplatform.model.QuizSession;
import com.college.quizplatform.repository.QuizRepository;
import com.college.quizplatform.repository.QuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class QuizSessionService {

    private final QuizSessionRepository sessionRepository;
    private final QuizRepository quizRepository;

    public QuizSession createSession(CreateSessionRequest request) {

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.isActive()) {
            throw new RuntimeException("Quiz is not active");
        }

        if (request.getScheduledEndTime().isBefore(request.getScheduledStartTime())) {
            throw new RuntimeException("Invalid time window");
        }

        QuizSession session = QuizSession.builder()
                .quizId(request.getQuizId())
                .sessionName(request.getSessionName())
                .scheduledStartTime(request.getScheduledStartTime())
                .scheduledEndTime(request.getScheduledEndTime())
                .started(false)
                .ended(false)
                .allowedStudentIds(request.getAllowedStudentIds())
                .createdAt(Instant.now())
                .build();

        return sessionRepository.save(session);
    }

    public QuizSession startSession(String sessionId) {

        QuizSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setStarted(true);

        return sessionRepository.save(session);
    }

    public QuizSession endSession(String sessionId) {

        QuizSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setEnded(true);

        return sessionRepository.save(session);
    }
}