package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    public UsersController(
            UserService userService
    ) {
        this.userService = userService;
    }
    @GetMapping()
    public ResponseEntity<List<User>> register() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
