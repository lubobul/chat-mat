package com.chat_mat_rest_service.controllers;

import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.dtos.rest.RestMessageResponse;
import com.chat_mat_rest_service.services.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private final FriendService friendService;

    public FriendsController(FriendService friendService) {
        this.friendService = friendService;
    }

    //Потребителя може да види във всеки един момент всички канали в които членува, както и всички приятели, които е добавил
    @Operation(
            summary = "Get a list of the user's friends",
            description = "Allows the authenticated user to see all the friends they have added, optionally filtered."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of friends"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameter")
    })
    @GetMapping
    public ResponseEntity<Page<UserDto>> getFriends(
            @RequestParam(value = "filter", required = false)
            @Parameter(description = "Optional filter string to search for specific friends") String filter,
            Pageable pageable
    ) {
        Page<UserDto> friends = friendService.getFriends(filter, pageable);
        return ResponseEntity.ok(friends);
    }

    //Потребител може да добави друг потребител
    @Operation(
            summary = "Add a new friend",
            description = "Allows the authenticated user to add another user as a friend."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added friend"),
            @ApiResponse(responseCode = "404", description = "User to be added as a friend not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/{friendId}")
    public ResponseEntity<RestMessageResponse> addFriend(@PathVariable
                                                         @Parameter(description = "ID of the user to add as a friend") Long friendId) {
        friendService.addFriend(friendId);
        return ResponseEntity.ok(new RestMessageResponse("Friend added successfully"));
    }

    @Operation(
            summary = "Remove a friend",
            description = "Allows the authenticated user to remove a friend from their list."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed friend"),
            @ApiResponse(responseCode = "404", description = "Friend not found")
    })
    @DeleteMapping("/{friendId}")
    public ResponseEntity<RestMessageResponse> removeFriend(@PathVariable
                                                            @Parameter(description = "ID of the friend to be removed") Long friendId) {
        friendService.removeFriend(friendId);
        return ResponseEntity.ok(new RestMessageResponse("Friend removed successfully"));
    }

    @Operation(
            summary = "Verify multiple friends",
            description = "Allows the user to verify a list of friends by their IDs."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified friends"),
            @ApiResponse(responseCode = "404", description = "One or more friends not found")
    })
    @PostMapping("/verify")
    public ResponseEntity<List<UserDto>> getFriendsForIds(
            @RequestBody
            @Parameter(description = "List of friend IDs to verify") List<Long> friendIds
    ) {
        List<UserDto> verifiedFriends = friendService.verifyFriends(friendIds);
        return ResponseEntity.ok(verifiedFriends);
    }
}
