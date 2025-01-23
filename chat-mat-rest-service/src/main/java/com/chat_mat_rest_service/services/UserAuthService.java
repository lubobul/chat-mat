package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.auth.JwtUtil;
import com.chat_mat_rest_service.dtos.auth.JwtResponse;
import com.chat_mat_rest_service.dtos.auth.LoginRequest;
import com.chat_mat_rest_service.dtos.auth.RegisterRequest;
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
    private final JwtUtil jwtUtil;


    public UserAuthService(
            UserRepository userRepository,
            UserSecretRepository userSecretRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.userSecretRepository = userSecretRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("The email you entered already exists.");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("The username you entered already exists.");
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

    public JwtResponse login(LoginRequest request) {
        validateLoginRequest(request);

        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("The email or password you entered is incorrect.");
        }

        Optional<UserSecret> userSecret = userSecretRepository.findByUserId(user.get().getId());

        if (userSecret.isEmpty()) {
            throw new IllegalArgumentException("The email or password you entered is incorrect.");
        }

        if (passwordEncoder.matches(request.getPassword(), userSecret.get().getPassword())) {
            return new JwtResponse(jwtUtil.generateToken(user.get().getEmail()));
        }

        throw new IllegalArgumentException("The email or password you entered is incorrect.");
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
