package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.auth.JwtResponse;
import com.chat_mat_rest_service.dtos.auth.LoginRequest;
import com.chat_mat_rest_service.dtos.auth.RegisterRequest;
import com.chat_mat_rest_service.dtos.rest.RestMessageResponse;
import com.chat_mat_rest_service.services.UserAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAuthService userAuthService;

    public AuthController(
            UserAuthService userAuthService
    ) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<RestMessageResponse> register(@RequestBody RegisterRequest request) {
        userAuthService.register(request);
        return ResponseEntity.ok(new RestMessageResponse("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        JwtResponse jwt = userAuthService.login(request);
        return ResponseEntity.ok(jwt);
    }
}

