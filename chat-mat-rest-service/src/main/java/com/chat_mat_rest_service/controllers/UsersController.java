package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.entities.UserDto;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(value = "filter", required = false) String filter,
            Pageable pageable
    ) {
        Page<UserDto> users = userService.getUsers(filter, pageable);
        return ResponseEntity.ok(users);
    }
}
