package com.college.quizplatform.service;

import com.college.quizplatform.dto.quiz.QuestionResponse;
import com.college.quizplatform.dto.quiz.QuizResponse;
import com.college.quizplatform.model.Question;
import com.college.quizplatform.model.Quiz;
import com.college.quizplatform.repository.QuestionRepository;
import com.college.quizplatform.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentQuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizResponse getQuizForStudent(String quizId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Safer query method
        List<Question> questions = questionRepository.findByQuizId(quizId);

        List<QuestionResponse> safeQuestions =
                questions.stream()
                        .map(q -> QuestionResponse.builder()
                                .id(q.getId())
                                .questionText(q.getQuestionText())
                                .options(q.getOptions())
                                .category(q.getCategory())
                                .build())
                        .toList();

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .topic(quiz.getTopic()) // FIX: Added
                .duration(quiz.getDuration()) // FIX: Added
                .description(quiz.getDescription())
                .active(quiz.isActive())
                .createdAt(quiz.getCreatedAt())
                .questions(safeQuestions)
                .build();
    }
}