package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("""
    SELECT c
    FROM Chat c
    JOIN c.participants p1
    WHERE c.isChannel = false
      AND c.owner.id = :currentUserId
      AND p1.id = :participantId
      AND c.deleted = false
""")
    List<Chat> findNonChannelChatsByParticipants(
            @Param("currentUserId") Long currentUserId,
            @Param("participantId") Long participantId
    );

    // Fetch chats where the user is the owner or a participant with a title filter
    @Query("""
        SELECT DISTINCT c
        FROM Chat c
        LEFT JOIN c.participants p
        WHERE (c.owner.id = :userId OR p.id = :userId)
          AND LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%'))
          AND c.deleted = false
          AND c.isChannel = :isChannel
    """)
    Page<Chat> findByOwnerIdOrParticipantIdAndTitleContainingIgnoreCase(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("isChannel") boolean isChannel,
            Pageable pageable
    );

    // Fetch chats where the user is the owner or a participant
    @Query("""
        SELECT DISTINCT c
        FROM Chat c
        LEFT JOIN c.participants p
        WHERE (c.owner.id = :userId OR p.id = :userId)
        AND c.deleted = false
        AND c.isChannel = :isChannel
    """)
    Page<Chat> findByOwnerIdOrParticipantId(
            @Param("userId") Long userId,
            @Param("isChannel") boolean isChannel,
            Pageable pageable
    );

    @Query("""
        SELECT c
        FROM Chat c
        LEFT JOIN c.participants p
        WHERE c.id = :chatId 
        AND (c.owner.id = :userId OR p.id = :userId)
        AND c.deleted = false
    """)
    Optional<Chat> findByIdAndOwnerIdOrParticipantId(@Param("chatId") Long chatId, @Param("userId") Long userId);

}