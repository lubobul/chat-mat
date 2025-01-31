package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.requests.UpdateUserRequest;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Update user profile details.
     *
     * @param updateUserRequest The data for updating the user profile
     * @return The updated user data
     */
    @Operation(
            summary = "Update user profile",
            description = "Allows the authenticated user to update their profile details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user profile"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping
    public ResponseEntity<UserDto> updateUserProfile(
            @RequestBody
            @Parameter(description = "Request body containing user profile data to update")
            UpdateUserRequest updateUserRequest
    ) {
        UserDto updatedUser = userService.updateProfile(updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete the user's profile.
     *
     * @return No content (204) indicating successful deletion
     */
    @Operation(
            summary = "Delete user profile",
            description = "Allows the authenticated user to delete their profile permanently."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user profile"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, if the user is not authenticated")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteUserProfile() {
        userService.deleteProfile();
        return ResponseEntity.noContent().build();
    }
}
