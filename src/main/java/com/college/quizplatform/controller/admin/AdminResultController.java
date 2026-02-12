package com.college.quizplatform.controller.admin;

import com.college.quizplatform.dto.admin.AdminResultResponse;
import com.college.quizplatform.model.Attempt;
import com.college.quizplatform.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/admin/results")
@RequiredArgsConstructor
public class AdminResultController {

    private final AttemptService attemptService;

    @GetMapping("/{sessionId}")
    public List<AdminResultResponse> getResults(@PathVariable String sessionId) {

        List<Attempt> attempts = attemptService.getLeaderboard(sessionId);

        AtomicInteger rankCounter = new AtomicInteger(1);

        return attempts.stream()
                .map(a -> AdminResultResponse.builder()
                        .rank(rankCounter.getAndIncrement())
                        .studentId(a.getStudentId())
                        .score(a.getScore())
                        .submitted(a.isSubmitted())
                        .build())
                .toList();
    }
}