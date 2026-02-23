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

        // Ensuring role matches the Enum format ROLE_ADMIN or ROLE_STUDENT
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
    // Updated login method with basic error handling
    public LoginResponse login(LoginRequest request) {
        try {
            // 1. Authenticate using Spring Security
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // 2. Extract authenticated user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 3. Generate JWT Token
            String token = jwtTokenProvider.generateToken(userDetails);

            // 4. Extract the user's role string (e.g., "ROLE_ADMIN")
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            // 5. Extract name for the Frontend Dashboard
            String name = userDetails.getName();

            return new LoginResponse(token, role, name);

        } catch (org.springframework.security.core.AuthenticationException e) {
            // This ensures the frontend receives a 401 Unauthorized instead of a 500
            throw new RuntimeException("Invalid email or password");
        }
    }
}