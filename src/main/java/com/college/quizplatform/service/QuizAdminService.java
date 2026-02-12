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
                .marksPerQuestion(request.getMarksPerQuestion())
                .negativeMarks(request.getNegativeMarks())
                .questionIds(new ArrayList<>())
                .active(true)
                .createdAt(Instant.now())
                .build();

        return quizRepository.save(quiz);
    }

    // ================= ADD QUESTION =================
    public Question addQuestion(CreateQuestionRequest request) {

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (request.getCorrectAnswerIndex() < 0 ||
                request.getCorrectAnswerIndex() >= request.getOptions().size()) {
            throw new RuntimeException("Invalid correctAnswerIndex");
        }

        Question question = Question.builder()
                .quizId(request.getQuizId())
                .questionText(request.getQuestionText())
                .options(request.getOptions())
                .correctAnswerIndex(request.getCorrectAnswerIndex())
                .category(request.getCategory())
                .timeLimitSeconds(request.getTimeLimitSeconds())
                .createdAt(Instant.now())
                .build();

        Question savedQuestion = questionRepository.save(question);

        quiz.getQuestionIds().add(savedQuestion.getId());
        quizRepository.save(quiz);

        return savedQuestion;
    }

    // ================= GET QUIZ WITH QUESTIONS =================
    public QuizResponse getQuizById(String quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Question> questions = questionRepository.findByQuizId(quizId);

        List<QuestionResponse> questionResponses = questions.stream()
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

    // ================= ENABLE / DISABLE QUIZ =================
    public Quiz toggleQuizStatus(String quizId, boolean active) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setActive(active);

        return quizRepository.save(quiz);
    }

    // ================= LIST ALL QUIZZES =================
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    // ================= BULK ADD QUESTIONS =================
    public List<Question> addQuestionsBulk(List<CreateQuestionRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new RuntimeException("Question list cannot be empty");
        }

        String quizId = requests.get(0).getQuizId();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Question> questionsToSave = new ArrayList<>();

        for (CreateQuestionRequest request : requests) {

            if (!quizId.equals(request.getQuizId())) {
                throw new RuntimeException("All questions must belong to same quiz");
            }

            if (request.getCorrectAnswerIndex() < 0 ||
                    request.getCorrectAnswerIndex() >= request.getOptions().size()) {
                throw new RuntimeException("Invalid correctAnswerIndex");
            }

            Question question = Question.builder()
                    .quizId(request.getQuizId())
                    .questionText(request.getQuestionText())
                    .options(request.getOptions())
                    .correctAnswerIndex(request.getCorrectAnswerIndex())
                    .category(request.getCategory())
                    .timeLimitSeconds(request.getTimeLimitSeconds())
                    .createdAt(Instant.now())
                    .build();

            questionsToSave.add(question);
        }

        // Save all at once
        List<Question> savedQuestions = questionRepository.saveAll(questionsToSave);

        // Update quiz questionIds
        for (Question q : savedQuestions) {
            quiz.getQuestionIds().add(q.getId());
        }

        quizRepository.save(quiz);

        return savedQuestions;
    }
}