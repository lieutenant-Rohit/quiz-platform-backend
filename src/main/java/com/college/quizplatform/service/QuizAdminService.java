package com.college.quizplatform.service;

import com.college.quizplatform.dto.question.CreateQuestionRequest;
import com.college.quizplatform.dto.quiz.CreateQuizRequest;
import com.college.quizplatform.dto.quiz.QuestionResponse;
import com.college.quizplatform.dto.quiz.QuizResponse;
import com.college.quizplatform.model.Question;
import com.college.quizplatform.model.Quiz;
import com.college.quizplatform.repository.QuestionRepository;
import com.college.quizplatform.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizAdminService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    // ================= CREATE QUIZ =================
    public Quiz createQuiz(CreateQuizRequest request) {

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .questionIds(new ArrayList<>())
                .active(true)
                .createdAt(Instant.now())
                .build();

        return quizRepository.save(quiz);
    }

    // ================= ADD QUESTION =================
    public Question addQuestion(CreateQuestionRequest request) {

        // 1️⃣ Check quiz exists
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() ->
                        new RuntimeException("Quiz not found"));

        // 2️⃣ Validate correctAnswerIndex
        if (request.getCorrectAnswerIndex() < 0 ||
                request.getCorrectAnswerIndex() >= request.getOptions().size()) {
            throw new RuntimeException("Invalid correctAnswerIndex");
        }

        // 3️⃣ Save question
        Question question = Question.builder()
                .quizId(request.getQuizId())
                .questionText(request.getQuestionText())
                .options(request.getOptions())
                .correctAnswerIndex(request.getCorrectAnswerIndex())
                .category(request.getCategory())
                .createdAt(Instant.now())
                .build();

        Question savedQuestion = questionRepository.save(question);

        // 4️⃣ Add question ID to quiz
        quiz.getQuestionIds().add(savedQuestion.getId());
        quizRepository.save(quiz);

        return savedQuestion;
    }
    // ================= GET QUIZ WITH QUESTIONS =================
    public QuizResponse getQuizById(String quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        var questions = questionRepository.findByQuizId(quizId);

        var questionResponses = questions.stream()
                .map(q -> QuestionResponse.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .options(q.getOptions())
                        .correctAnswerIndex(q.getCorrectAnswerIndex())
                        .category(q.getCategory())
                        .build())
                .toList();

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .active(quiz.isActive())
                .createdAt(quiz.getCreatedAt())
                .questions(questionResponses)
                .build();
    }
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
    public Quiz toggleQuizStatus(String quizId, boolean active) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setActive(active);

        return quizRepository.save(quiz);
    }
}