package com.chat_mat_rest_service.controllers;

import com.chat_mat_rest_service.auth.JwtUtil;
import com.chat_mat_rest_service.dtos.LoginRequest;
import com.chat_mat_rest_service.dtos.RegisterRequest;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.services.UserAuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAuthService userAuthService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserAuthService userAuthService,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.userAuthService = userAuthService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userAuthService.register(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        Optional<User> user = userAuthService.login(request);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.get().getEmail());
        return ResponseEntity.ok(token);
    }
}
