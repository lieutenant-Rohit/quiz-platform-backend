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
                .topic(request.getTopic())
                .duration(request.getDuration())
                .description(request.getDescription())
                .marksPerQuestion(request.getMarksPerQuestion())
                .negativeMarks(request.getNegativeMarks())
                .totalMarks(request.getTotalMarks())
                .questionIds(new ArrayList<>())
                .active(false)
                .createdAt(Instant.now())
                .build();

        return quizRepository.save(quiz);
    }

    // ================= ADD QUESTION =================
    public Question addQuestion(CreateQuestionRequest request) {

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<String> options = request.getOptions();
        int correctIndex = request.getCorrectAnswerIndex();

        // Extra safety checks
        if (options == null || options.isEmpty()) {
            throw new RuntimeException("Question must have options");
        }
        if (correctIndex < 0 || correctIndex >= options.size()) {
            throw new RuntimeException("Invalid correct answer index");
        }

        Question question = Question.builder()
                .quizId(request.getQuizId())
                .questionText(request.getQuestionText())
                .options(options)
                .correctAnswerIndex(correctIndex)
                .category(request.getCategory() != null ? request.getCategory() : "General")
                .timeLimitSeconds(request.getTimeLimitSeconds())
                .createdAt(Instant.now())
                .build();

        Question savedQuestion = questionRepository.save(question);

        if (quiz.getQuestionIds() == null) {
            quiz.setQuestionIds(new ArrayList<>());
        }
        quiz.getQuestionIds().add(savedQuestion.getId());
        quizRepository.save(quiz);

        return savedQuestion;
    }

    // ================= GET QUIZ WITH QUESTIONS =================
    public QuizResponse getQuizById(String quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found: " + quizId));

        List<Question> questions = questionRepository.findByQuizId(quizId);
        List<QuestionResponse> questionResponses = new ArrayList<>();

        // FIX: Fortified mapping to prevent NullPointerExceptions
        if (questions != null && !questions.isEmpty()) {
            questionResponses = questions.stream()
                    .map(q -> QuestionResponse.builder()
                            .id(q.getId())
                            .questionText(q.getQuestionText() != null ? q.getQuestionText() : "Untitled")
                            .options(q.getOptions() != null ? q.getOptions() : new ArrayList<>())
                            .correctAnswerIndex(q.getCorrectAnswerIndex())
                            .category(q.getCategory() != null ? q.getCategory() : "General")
                            .build())
                    .toList();
        }

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .topic(quiz.getTopic())
                .duration(quiz.getDuration())
                .description(quiz.getDescription())
                .active(quiz.isActive())
                .createdAt(quiz.getCreatedAt() != null ? quiz.getCreatedAt() : Instant.now())
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
        // Keeping bulk logic intact but guarded
        if (requests == null || requests.isEmpty()) throw new RuntimeException("Empty list");
        String quizId = requests.get(0).getQuizId();
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Question> questionsToSave = new ArrayList<>();
        for (CreateQuestionRequest request : requests) {
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

        List<Question> savedQuestions = questionRepository.saveAll(questionsToSave);

        if (quiz.getQuestionIds() == null) quiz.setQuestionIds(new ArrayList<>());
        for (Question q : savedQuestions) quiz.getQuestionIds().add(q.getId());

        quizRepository.save(quiz);
        return savedQuestions;
    }
}