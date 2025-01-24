package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.entities.UserDto;
import com.chat_mat_rest_service.entities.Friend;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.services.FriendService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> addFriend(@PathVariable Long friendId) {
        friendService.addFriend(friendId);
        return ResponseEntity.ok("Friend added successfully");
    }
}
