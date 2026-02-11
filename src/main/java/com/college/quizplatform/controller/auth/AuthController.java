package com.college.quizplatform.controller.auth;

import com.college.quizplatform.dto.auth.LoginRequest;
import com.college.quizplatform.dto.auth.LoginResponse;
import com.college.quizplatform.dto.auth.RegisterRequest;
import com.college.quizplatform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ================= REGISTER =================
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
    }

    // ================= LOGIN (STEP 9) =================
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}