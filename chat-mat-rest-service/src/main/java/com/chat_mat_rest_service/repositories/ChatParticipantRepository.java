package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.Chat;
import com.chat_mat_rest_service.entities.ChatParticipant;
import com.chat_mat_rest_service.entities.ChatParticipantId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
}