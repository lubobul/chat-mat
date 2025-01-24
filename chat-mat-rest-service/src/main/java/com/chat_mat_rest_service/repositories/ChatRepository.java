package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}