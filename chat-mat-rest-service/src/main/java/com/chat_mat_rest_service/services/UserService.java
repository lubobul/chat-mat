package com.chat_mat_rest_service.services;
import com.chat_mat_rest_service.dtos.entities.UserDto;
import com.chat_mat_rest_service.dtos.mappers.UserMapper;
import com.chat_mat_rest_service.entities.Friend;
import com.chat_mat_rest_service.repositories.FriendRepository;
import com.chat_mat_rest_service.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chat_mat_rest_service.common.SecurityContextHelper.getAuthenticatedUserId;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, FriendRepository friendRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.friendRepository = friendRepository;
    }

    public Page<UserDto> getUsersWithoutFriendsInfo(String filter, Pageable pageable, boolean excludeSelf) {
        Long authenticatedUserId = excludeSelf ? getAuthenticatedUserId() : null;

        Page<UserDto> userPage = null;

        if (filter != null) {
            // Split filter into individual conditions
            String[] conditions = filter.split(",");
            String username = null;
            String email = null;

            for (String condition : conditions) {
                if (condition.startsWith("username==")) {
                    username = condition.substring("username==".length());
                } else if (condition.startsWith("email==")) {
                    email = condition.substring("email==".length());
                }
            }


            // Apply filtering logic with exclusion
            if (username != null && email != null) {
                if (authenticatedUserId != null) {
                    return userRepository
                            .findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCaseAndIdNot(username, email, authenticatedUserId, pageable)
                            .map(userMapper::toDto);
                } else {
                    return userRepository
                            .findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(username, email, pageable)
                            .map(userMapper::toDto);
                }

            } else if (username != null) {
                if (authenticatedUserId != null) {
                    return userRepository
                            .findByUsernameContainingIgnoreCaseAndIdNot(username, authenticatedUserId, pageable)
                            .map(userMapper::toDto);
                } else {
                    return userRepository
                            .findByUsernameContainingIgnoreCase(username, pageable)
                            .map(userMapper::toDto);
                }
            } else if (email != null) {
                if (authenticatedUserId != null) {
                    return userRepository
                            .findByEmailContainingIgnoreCaseAndIdNot(email, authenticatedUserId, pageable)
                            .map(userMapper::toDto);
                } else {
                    return userRepository
                            .findByEmailContainingIgnoreCase(email, pageable)
                            .map(userMapper::toDto);
                }

            }
        }

        // Default to returning all users, excluding the authenticated user if needed
        if (authenticatedUserId != null) {
            return userRepository.findByIdNot(authenticatedUserId, pageable).map(userMapper::toDto);
        }

        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    public Page<UserDto> getUsersWithFriendsInfo(String filter, Pageable pageable, boolean excludeSelf){
        // Fetch the users without friend info
        Page<UserDto> userPageWithoutFriendsInfo = this.getUsersWithoutFriendsInfo(filter, pageable, excludeSelf);
        List<Long> userIds = userPageWithoutFriendsInfo.stream().map(UserDto::getId).toList();

        // Fetch friends matching the user IDs
        List<Friend> friends = this.friendRepository.findByUserIdAndFriendIds(getAuthenticatedUserId(), userIds);

        // Collect friend IDs into a Set for quick lookup
        Set<Long> friendIds = friends.stream()
                .map(friend -> friend.getFriend().getId())
                .collect(Collectors.toSet());

        // Map over the user page and set the isFriendOfYours property
        List<UserDto> updatedUserDtos = userPageWithoutFriendsInfo.stream()
                .peek(userDto -> userDto.setFriendOfYours(friendIds.contains(userDto.getId())))
                .toList();

        // Return the updated list as a Page
        return new PageImpl<>(updatedUserDtos, pageable, userPageWithoutFriendsInfo.getTotalElements());
    }

    public Page<UserDto> getUsers(String filter, Pageable pageable, boolean excludeSelf, boolean withFriendsInfo){
        if(withFriendsInfo){
            return getUsersWithFriendsInfo(filter, pageable, excludeSelf);
        }else{
            return this.getUsersWithoutFriendsInfo(filter, pageable, excludeSelf);
        }
    }
}
