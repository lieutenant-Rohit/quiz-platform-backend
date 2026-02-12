package com.college.quizplatform.controller.student;

import com.college.quizplatform.dto.quiz.QuizResponse;
import com.college.quizplatform.service.StudentQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student/quizzes")
@RequiredArgsConstructor
public class StudentQuizController {

    private final StudentQuizService studentQuizService;

    @GetMapping("/{quizId}")
    public QuizResponse getQuiz(@PathVariable String quizId) {
        return studentQuizService.getQuizForStudent(quizId);
    }
}