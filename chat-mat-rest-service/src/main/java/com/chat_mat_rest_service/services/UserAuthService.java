package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.dtos.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.entities.UserSecret;
import com.chat_mat_rest_service.repositories.UserRepository;
import com.chat_mat_rest_service.repositories.UserSecretRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthService {
    private final UserRepository userRepository;
    private final UserSecretRepository userSecretRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserRepository userRepository, UserSecretRepository userSecretRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userSecretRepository = userSecretRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Create and save the User entity with the avatar
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        userRepository.save(user);

        // Create and save the UserSecret entity with the password
        UserSecret userSecret = new UserSecret();
        userSecret.setUser(user);
        userSecret.setPassword(passwordEncoder.encode(password)); // Hash the password
        userSecretRepository.save(userSecret);
    }

    public Optional<User> login(LoginRequest request){
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if(user.isEmpty()){
            return Optional.empty();
        }

        Optional<UserSecret> userSecret = userSecretRepository.findByUserId(user.get().getId());

        if(userSecret.isEmpty()){
            return Optional.empty();
        }

        if (passwordEncoder.matches(request.getPassword(), userSecret.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }
    public Optional<UserSecret> findUserSecretByUserId(Long userId) {
        return userSecretRepository.findByUserId(userId);
    }
}
