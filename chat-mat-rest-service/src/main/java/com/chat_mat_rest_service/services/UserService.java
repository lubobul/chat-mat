package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.dtos.LoginRequest;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.entities.UserSecret;
import com.chat_mat_rest_service.repositories.UserRepository;
import com.chat_mat_rest_service.repositories.UserSecretRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
