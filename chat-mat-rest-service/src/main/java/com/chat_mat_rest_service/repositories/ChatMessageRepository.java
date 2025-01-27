package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}