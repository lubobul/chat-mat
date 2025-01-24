package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.common.HttpConstants;
import com.chat_mat_rest_service.dtos.auth.JwtResponse;
import com.chat_mat_rest_service.dtos.auth.LoginRequest;
import com.chat_mat_rest_service.dtos.auth.RegisterRequest;
import com.chat_mat_rest_service.dtos.rest.RestMessageResponse;
import com.chat_mat_rest_service.services.UserAuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
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
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        JwtResponse jwtToken = userAuthService.login(request); // Obtain the JWT token

        // Set the JWT token as an HTTP-only secure cookie
        ResponseCookie jwtCookie = ResponseCookie.from(HttpConstants.JWT_TOKEN, jwtToken.getToken())
                .httpOnly(true) // Prevent access from JavaScript
                .secure(true) // Use HTTPS
                .sameSite("Strict") // SameSite policy for CSRF protection
                .path("/") // Available for all paths
                .maxAge(10 * 60 * 60) // 10 hours
                .build();

        response.addHeader("Set-Cookie", jwtCookie.toString());
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<RestMessageResponse> logout(HttpServletResponse response) {
        // Clear the JWT cookie by setting maxAge to 0
        ResponseCookie jwtCookie = ResponseCookie.from(HttpConstants.JWT_TOKEN, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // Clear cookie
                .build();

        response.addHeader("Set-Cookie", jwtCookie.toString());
        return ResponseEntity.ok(new RestMessageResponse("Logged out successfully"));
    }
}

