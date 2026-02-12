package com.college.quizplatform.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.college.quizplatform.dto.attempt.StartAttemptResponse;
import com.college.quizplatform.dto.attempt.SubmitAnswerRequest;
import com.college.quizplatform.model.Attempt;
import com.college.quizplatform.model.Question;
import com.college.quizplatform.model.Quiz;
import com.college.quizplatform.model.QuizSession;
import com.college.quizplatform.repository.AttemptRepository;
import com.college.quizplatform.repository.QuestionRepository;
import com.college.quizplatform.repository.QuizSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.college.quizplatform.repository.QuizRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final QuizSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    // ================= START ATTEMPT =================
    // ================= START ATTEMPT =================
    public StartAttemptResponse startAttempt(String sessionId) {

        String studentId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

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
                .questionStartTimes(new HashMap<>())   // ✅ initialize here
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
    // ================= SAVE ANSWER WITH PER-QUESTION TIMER =================
    public void submitAnswer(SubmitAnswerRequest request) {

        Attempt attempt = attemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.isSubmitted()) {
            throw new RuntimeException("Attempt already submitted");
        }

        QuizSession session = sessionRepository.findById(attempt.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 🔒 Hard session-level time enforcement
        if (Instant.now().isAfter(session.getScheduledEndTime()) || session.isEnded()) {
            throw new RuntimeException("Session time expired");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // ================= PER-QUESTION TIMER LOGIC =================

        // If first time answering this question → start timer
        if (!attempt.getQuestionStartTimes().containsKey(request.getQuestionId())) {
            attempt.getQuestionStartTimes()
                    .put(request.getQuestionId(), Instant.now());
        }

        Instant startTime = attempt.getQuestionStartTimes()
                .get(request.getQuestionId());

        if (startTime != null && question.getTimeLimitSeconds() > 0) {

            long secondsElapsed = Instant.now().getEpochSecond()
                    - startTime.getEpochSecond();

            if (secondsElapsed > question.getTimeLimitSeconds()) {
                throw new RuntimeException("Question time expired");
            }
        }

        // Save answer
        attempt.getAnswers().put(
                request.getQuestionId(),
                request.getSelectedOptionIndex()
        );

        attemptRepository.save(attempt);
    }
    // ================= SUBMIT ATTEMPT =================
    // ================= SUBMIT ATTEMPT WITH NEGATIVE MARKING =================
    public int submitAttempt(String attemptId) {

        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found"));

        if (attempt.isSubmitted()) {
            throw new RuntimeException("Already submitted");
        }

        QuizSession session = sessionRepository.findById(attempt.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Quiz quiz = quizRepository.findById(attempt.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int score = 0;

        for (var entry : attempt.getAnswers().entrySet()) {

            Question question = questionRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            if (question.getCorrectAnswerIndex() == entry.getValue()) {

                // ✅ Correct answer
                score += quiz.getMarksPerQuestion();

            } else {

                // ❌ Wrong answer (negative marking)
                score -= quiz.getNegativeMarks();
            }
        }

        // Optional safety: prevent negative total score
        if (score < 0) {
            score = 0;
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
    // ================= PAGINATED LEADERBOARD =================
    public Page<Attempt> getLeaderboard(String sessionId, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("score"),
                        Sort.Order.asc("submittedAt")
                )
        );

        return attemptRepository
                .findBySessionIdAndSubmittedTrue(sessionId, pageable);
    }


}