package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.auth.JwtUserDetails;
import com.chat_mat_rest_service.dtos.entities.UserDto;
import com.chat_mat_rest_service.dtos.mappers.UserMapper;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Page<UserDto> getUsers(String filter, Pageable pageable) {
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

            // Apply filtering logic
            if (username != null && email != null) {
                return userRepository
                        .findByUsernameContainingIgnoreCaseAndEmailContainingIgnoreCase(username, email, pageable)
                        .map(userMapper::toDto);
            } else if (username != null) {
                return userRepository.findByUsernameContainingIgnoreCase(username, pageable)
                        .map(userMapper::toDto);
            } else if (email != null) {
                return userRepository.findByEmailContainingIgnoreCase(email, pageable)
                        .map(userMapper::toDto);
            }
        }

        // Default to returning all users
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

}
