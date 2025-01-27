package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("""
    SELECT m 
    FROM ChatMessage m 
    WHERE m.chat.id = :chatId
    ORDER BY m.createdAt DESC
""")
    Page<ChatMessage> findMessagesByChatId(@Param("chatId") Long chatId, Pageable pageable);

}