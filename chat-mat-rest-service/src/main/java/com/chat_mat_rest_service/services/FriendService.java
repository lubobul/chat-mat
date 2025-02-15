package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.custom.exceptions.ResourceNotFoundException;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.dtos.mappers.UserMapper;
import com.chat_mat_rest_service.entities.Friend;
import com.chat_mat_rest_service.entities.FriendId;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.repositories.ChatRepository;
import com.chat_mat_rest_service.repositories.FriendRepository;
import com.chat_mat_rest_service.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.chat_mat_rest_service.common.SecurityContextHelper.getAuthenticatedUserId;

@Service
public class FriendService {

    private final FriendRepository friendRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public FriendService(FriendRepository friendRepository, UserRepository userRepository, ChatRepository chatRepository, UserMapper userMapper) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.chatRepository = chatRepository;
    }

    public Page<UserDto> getFriends(String filter, Pageable pageable) {
        Long userId = getAuthenticatedUserId();
        // Fetch friends based on filter
        Page<Friend> friendsPage;

        if (filter != null) {
            // Split filter into individual conditions
            String[] conditions = filter.split(",");
            String username = null;

            for (String condition : conditions) {
                if (condition.startsWith("username==")) {
                    username = condition.substring("username==".length());
                }
            }
            friendsPage = friendRepository.findNonDeletedFriendsByUserId(userId, username, pageable);
        } else {
            friendsPage = friendRepository.findNonDeletedFriendsByUserId(userId, null, pageable);
        }


        return friendsPage.map(friend -> userMapper.toDto(friend.getFriend()));
    }

    public void addFriend(Long friendId) {
        Long userId = getAuthenticatedUserId();

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("You cannot add yourself as a friend");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getFriends().contains(friend)) {
            throw new IllegalArgumentException("This user is already your friend");
        }

        friendRepository.addFriend(userId, friendId); // Custom query for adding a friend
    }

    public void removeFriend(Long friendId) {
        Long userId = getAuthenticatedUserId();

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("You cannot unfriend yourself");
        }

        // Check if the friendship exists
        FriendId friendIdKey = new FriendId(userId, friendId);
        if (!friendRepository.existsById(friendIdKey)) {
            throw new ResourceNotFoundException("This user is not your friend");
        }

        // Remove the friendship
        friendRepository.removeFriend(userId, friendId);

        // Soft delete applicable chats
        List<Long> chatIds = chatRepository.findChatsToSoftDelete(userId, friendId);
        if (!chatIds.isEmpty()) {
            chatRepository.softDeleteChats(chatIds);
        }
    }

    public List<UserDto> verifyFriends(List<Long> friendIds) {
        Long userId = getAuthenticatedUserId();
        return this.friendRepository
                .findByUserIdAndFriendIds(userId, friendIds)
                .stream()
                .map(friend -> userMapper.toDto(friend.getFriend()))
                .toList();
    }
}
