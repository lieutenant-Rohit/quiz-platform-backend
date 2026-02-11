package com.college.quizplatform.service;

import com.college.quizplatform.dto.attempt.StartAttemptResponse;
import com.college.quizplatform.dto.attempt.SubmitAnswerRequest;
import com.college.quizplatform.model.Attempt;
import com.college.quizplatform.model.Question;
import com.college.quizplatform.model.QuizSession;
import com.college.quizplatform.repository.AttemptRepository;
import com.college.quizplatform.repository.QuestionRepository;
import com.college.quizplatform.repository.QuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final QuizSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;

    // ================= START ATTEMPT =================
    public StartAttemptResponse startAttempt(String sessionId) {

        String studentId = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        QuizSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (!session.isStarted() || session.isEnded()) {
            throw new RuntimeException("Session not active");
        }

        if (Instant.now().isBefore(session.getScheduledStartTime()) ||
                Instant.now().isAfter(session.getScheduledEndTime())) {
            throw new RuntimeException("Outside time window");
        }

        attemptRepository.findBySessionIdAndStudentId(sessionId, studentId)
                .ifPresent(a -> {
                    throw new RuntimeException("Attempt already exists");
                });

        Attempt attempt = Attempt.builder()
                .sessionId(sessionId)
                .quizId(session.getQuizId())
                .studentId(studentId)
                .answers(new HashMap<>())
                .startedAt(Instant.now())
                .submitted(false)
                .score(0)
                .build();

        Attempt saved = attemptRepository.save(attempt);

        return StartAttemptResponse.builder()
                .attemptId(saved.getId())
                .sessionId(sessionId)
                .quizId(saved.getQuizId())
                .build();
    }

    // ================= SAVE ANSWER =================
    public void submitAnswer(SubmitAnswerRequest request) {

        Attempt attempt = attemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.isSubmitted()) {
            throw new RuntimeException("Attempt already submitted");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        attempt.getAnswers().put(
                request.getQuestionId(),
                request.getSelectedOptionIndex()
        );

        attemptRepository.save(attempt);
    }
    // ================= SUBMIT ATTEMPT =================
    public int submitAttempt(String attemptId) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.isSubmitted()) {
            throw new RuntimeException("Already submitted");
        }

        QuizSession session = sessionRepository.findById(attempt.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.isEnded()) {
            throw new RuntimeException("Session already ended");
        }

        int score = 0;

        for (var entry : attempt.getAnswers().entrySet()) {

            Question question = questionRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            if (question.getCorrectAnswerIndex() == entry.getValue()) {
                score++;
            }
        }

        attempt.setScore(score);
        attempt.setSubmitted(true);
        attempt.setSubmittedAt(Instant.now());

        attemptRepository.save(attempt);

        return score;
    }
    public List<Attempt> getLeaderboard(String sessionId) {
        return attemptRepository
                .findBySessionIdAndSubmittedTrueOrderByScoreDescSubmittedAtAsc(sessionId);
    }


}