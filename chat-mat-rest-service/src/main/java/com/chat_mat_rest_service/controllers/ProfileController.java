package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.requests.UpdateUserRequest;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(
            UserService userService
    ) {
        this.userService = userService;
    }

    // Update user details
    @PutMapping()
    public ResponseEntity<UserDto> updateUserProfile(
            @RequestBody UpdateUserRequest updateUserRequest
    ) {
        UserDto updatedUser = userService.updateProfile(updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete user by ID
    @DeleteMapping()
    public ResponseEntity<Void> deleteUserProfile() {
        userService.deleteProfile();
        return ResponseEntity.noContent().build();
    }
}
