package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("""
    SELECT c
    FROM Chat c
    JOIN c.participants p1
    WHERE c.isChannel = false
      AND c.owner.id = :currentUserId
      AND p1.id = :participantId
""")
    List<Chat> findNonChannelChatsByParticipants(
            @Param("currentUserId") Long currentUserId,
            @Param("participantId") Long participantId
    );
}