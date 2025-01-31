package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    //Потребител може да търси сред всички регистрирани в системата потребители

    /**
     * Get a paginated list of all users.
     *
     * @param filter Optional filter to search users by name or other attributes
     * @param excludeSelf Flag to exclude the current authenticated user from the list
     * @param withFriendsInfo Flag to include friend information with each user
     * @param pageable Pagination information (page number, page size, sorting)
     * @return A paginated list of user data
     */
    @Operation(
            summary = "Get a list of users",
            description = "Retrieve all registered users with optional filters, pagination, and additional friend information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users"),
            @ApiResponse(responseCode = "400", description = "Invalid filter or pagination parameters")
    })
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(value = "filter", required = false)
            @Parameter(description = "Filter to search users by name or other attributes")
            String filter,

            @RequestParam(value = "excludeSelf", required = false)
            @Parameter(description = "Flag to exclude the current authenticated user from the list")
            boolean excludeSelf,

            @RequestParam(value = "withFriendsInfo", required = false)
            @Parameter(description = "Flag to include friend information with each user")
            boolean withFriendsInfo,

            Pageable pageable
    ) {
        Page<UserDto> users = userService.getUsers(filter, pageable, excludeSelf, withFriendsInfo);
        return ResponseEntity.ok(users);
    }

    /**
     * Get a specific user by their ID.
     *
     * @param id The ID of the user
     * @return User data for the specified ID
     */
    @Operation(
            summary = "Get user by ID",
            description = "Retrieve detailed information for a specific user by their unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user information"),
            @ApiResponse(responseCode = "404", description = "User not found for the given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable("id")
            @Parameter(description = "The ID of the user to retrieve")
            Long id
    ) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}

