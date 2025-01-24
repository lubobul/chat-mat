package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.dtos.rest.RestMessageResponse;
import com.chat_mat_rest_service.services.FriendService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private final FriendService friendService;

    public FriendsController(
            FriendService friendService
    ) {
        this.friendService = friendService;
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getFriends(
            @RequestParam(value = "filter", required = false) String filter,
            Pageable pageable
    ) {
        Page<UserDto> friends = friendService.getFriends(filter, pageable);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/{friendId}")
    public ResponseEntity<RestMessageResponse> addFriend(@PathVariable Long friendId) {
        friendService.addFriend(friendId);
        return ResponseEntity.ok(new RestMessageResponse("Friend added successfully"));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<RestMessageResponse> removeFriend(@PathVariable Long friendId) {
        friendService.removeFriend(friendId);
        return ResponseEntity.ok(new RestMessageResponse("Friend removed successfully"));
    }

    @PostMapping("/verify")
    public ResponseEntity<List<UserDto>> getFriendsForIds(
            @RequestBody List<Long> friendIds
    ) {
        List<UserDto> verifiedFriends = friendService.verifyFriends(friendIds);
        return ResponseEntity.ok(verifiedFriends);
    }
}
