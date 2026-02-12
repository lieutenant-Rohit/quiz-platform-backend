package com.college.quizplatform.controller.admin;

import com.college.quizplatform.dto.question.CreateQuestionRequest;
import com.college.quizplatform.dto.quiz.CreateQuizRequest;
import com.college.quizplatform.dto.quiz.QuizResponse;
import com.college.quizplatform.model.Question;
import com.college.quizplatform.model.Quiz;
import com.college.quizplatform.service.QuizAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/quizzes")
@RequiredArgsConstructor
public class QuizAdminController {

    private final QuizAdminService quizAdminService;

    // ================= CREATE QUIZ =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Quiz createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        return quizAdminService.createQuiz(request);
    }

    // ================= ADD QUESTION =================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questions")
    public Question addQuestion(
            @Valid @RequestBody CreateQuestionRequest request) {

        return quizAdminService.addQuestion(request);
    }
    // ================= GET QUIZ =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{quizId}")
    public QuizResponse getQuiz(@PathVariable String quizId) {
        return quizAdminService.getQuizById(quizId);
    }

    // ================= LIST QUIZZES =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Quiz> listQuizzes() {
        return quizAdminService.getAllQuizzes();
    }

    // ================= ENABLE/DISABLE QUIZ =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{quizId}/status")
    public Quiz toggleStatus(
            @PathVariable String quizId,
            @RequestParam boolean active) {

        return quizAdminService.toggleQuizStatus(quizId, active);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questions/bulk")
    public List<Question> addQuestionsBulk(
            @RequestBody List<CreateQuestionRequest> requests) {

        return quizAdminService.addQuestionsBulk(requests);
    }
}