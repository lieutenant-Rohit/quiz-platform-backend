package com.college.quizplatform.controller.admin;

import com.college.quizplatform.model.Attempt;
import com.college.quizplatform.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/leaderboard")
@RequiredArgsConstructor
public class AttemptAdminController {

    private final AttemptService attemptService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{sessionId}")
    public Page<Attempt> leaderboard(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return attemptService.getLeaderboard(sessionId, page, size);
    }
}