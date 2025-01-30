package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.requests.UpdateUserRequest;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    public UsersController(
            UserService userService
    ) {
        this.userService = userService;
    }

    //Потребител може да търси сред всички регистрирани в системата потребители
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "excludeSelf", required = false) boolean excludeSelf,
            @RequestParam(value = "withFriendsInfo", required = false) boolean withFriendsInfo,
            Pageable pageable
    ) {
        Page<UserDto> users = userService.getUsers(filter, pageable, excludeSelf, withFriendsInfo);
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
