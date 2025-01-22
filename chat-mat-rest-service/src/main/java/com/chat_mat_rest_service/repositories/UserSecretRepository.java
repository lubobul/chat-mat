package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.UserSecret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSecretRepository extends JpaRepository<UserSecret, Long> {
    Optional<UserSecret> findByUserId(Long id);
}
