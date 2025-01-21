package com.chat_mat_rest_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    @GetMapping()
    public ResponseEntity<String> register() {
        return ResponseEntity.ok("Users: ---");
    }
}
