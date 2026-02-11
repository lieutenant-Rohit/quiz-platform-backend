package com.college.quizplatform.service;

import com.college.quizplatform.dto.auth.LoginRequest;
import com.college.quizplatform.dto.auth.LoginResponse;
import com.college.quizplatform.dto.auth.RegisterRequest;
import com.college.quizplatform.model.Role;
import com.college.quizplatform.model.User;
import com.college.quizplatform.repository.UserRepository;
import com.college.quizplatform.security.CustomUserDetails;
import com.college.quizplatform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // ================= REGISTER (Phase 1A) =================
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role = Role.valueOf("ROLE_" + request.getRole().toUpperCase());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);
    }

    // ================= LOGIN (STEP 8) =================
    public LoginResponse login(LoginRequest request) {

        // Authenticate using Spring Security
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Extract authenticated user
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        // Generate JWT
        String token = jwtTokenProvider.generateToken(userDetails);

        return new LoginResponse(token);
    }
}