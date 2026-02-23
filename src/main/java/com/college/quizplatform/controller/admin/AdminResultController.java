package com.college.quizplatform.controller.admin;

import com.college.quizplatform.dto.admin.AdminResultResponse;
import com.college.quizplatform.model.Attempt;
import com.college.quizplatform.model.User;
import com.college.quizplatform.repository.UserRepository;
import com.college.quizplatform.service.AttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/results")
@RequiredArgsConstructor
public class AdminResultController {

    private final AttemptService attemptService;
    private final UserRepository userRepository;

    @GetMapping("/{sessionId}")
    public List<AdminResultResponse> getResults(@PathVariable String sessionId) {

        List<Attempt> attempts = attemptService.getLeaderboard(sessionId);

        // 1. Extract unique student IDs
        Set<String> studentIds = attempts.stream()
                .map(Attempt::getStudentId)
                .collect(Collectors.toSet());

        // 2. Fetch all users in ONE query
        Map<String, User> userMap = userRepository.findAllById(studentIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        AtomicInteger rankCounter = new AtomicInteger(1);

        return attempts.stream()
                .map(a -> {
                    // 3. Look up user from memory map
                    User user = userMap.get(a.getStudentId());
                    String name = (user != null) ? user.getName() : "Unknown User";
                    String email = (user != null) ? user.getEmail() : "Unknown Email";

                    return AdminResultResponse.builder()
                            .rank(rankCounter.getAndIncrement())
                            .studentId(a.getStudentId())
                            .studentName(name)
                            .studentEmail(email)
                            .score(a.getScore())
                            .submitted(a.isSubmitted())
                            .build();
                })
                .toList();
    }
}