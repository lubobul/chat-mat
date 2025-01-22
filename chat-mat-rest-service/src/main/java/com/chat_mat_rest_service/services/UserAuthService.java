package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.dtos.LoginRequest;
import com.chat_mat_rest_service.dtos.RegisterRequest;
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

    public void register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }

        // Create and save the User entity with the avatar
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        // Create and save the UserSecret entity with the password
        UserSecret userSecret = new UserSecret();
        userSecret.setUser(user);
        userSecret.setPassword(passwordEncoder.encode(request.getPassword())); // Hash the password
        userSecretRepository.save(userSecret);
    }

    public Optional<User> login(LoginRequest request){
        validateLoginRequest(request);

        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if(user.isEmpty()){
            throw new IllegalArgumentException("Email or password are wrong.");
        }

        Optional<UserSecret> userSecret = userSecretRepository.findByUserId(user.get().getId());

        if(userSecret.isEmpty()){
            throw new IllegalArgumentException("Email or password are wrong.");
        }

        if (passwordEncoder.matches(request.getPassword(), userSecret.get().getPassword())) {
            return user;
        }

        throw new IllegalArgumentException("Email or password are wrong.");
    }
    public Optional<UserSecret> findUserSecretByUserId(Long userId) {
        return userSecretRepository.findByUserId(userId);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (isNullOrEmpty(request.getUsername()) ||
                isNullOrEmpty(request.getEmail()) ||
                isNullOrEmpty(request.getPassword())) {
            throw new IllegalArgumentException("Username, email, and password must not be empty");
        }

        String password = request.getPassword();
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).+$")) {
            throw new IllegalArgumentException("Password must contain at least one letter and one number");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (isNullOrEmpty(request.getEmail()) || isNullOrEmpty(request.getPassword())) {
            throw new IllegalArgumentException("Email and password must not be empty");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
