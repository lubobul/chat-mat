package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.Chat;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("""
    SELECT COUNT(cp) > 0
    FROM ChatParticipant cp
    WHERE cp.chat.id = :chatId
    AND cp.user.id = :currentUserId
    AND (cp.isAdmin = true OR cp.chat.owner.id = :currentUserId)
""")
    boolean existsByIdAndOwnerIdOrAdminId(@Param("chatId") Long chatId, @Param("currentUserId") Long currentUserId);

    boolean existsByIdAndOwnerId(Number chatId, Number currentUserId);
    @Query("""
    SELECT c
    FROM Chat c
    JOIN c.participants p1
    WHERE c.isChannel = false
      AND ((c.owner.id = :currentUserId AND p1.id = :participantId) OR (c.owner.id = :participantId AND p1.id = :currentUserId))
      AND c.deleted = false
""")
    List<Chat> findNonChannelChatsByParticipantsOrOwner(
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
          AND c.owner.deleted = false
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
        AND c.owner.deleted = false
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
        WHERE c.id = :chatId AND (c.owner.id = :userId OR p.id = :userId)
        AND c.owner.deleted = false
        AND c.deleted = false
    """)
    Optional<Chat> findByIdAndOwnerIdOrParticipantId(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("""
    SELECT c
    FROM Chat c
    JOIN ChatParticipant cp ON cp.chat.id = c.id
    WHERE c.id = :chatId
    AND cp.user.id = :currentUserId
    AND (cp.isAdmin = true OR c.owner.id = :currentUserId)
""")
    Optional<Chat> findByIdAndOwnerIdOrAdminId(@Param("chatId") Long chatId, @Param("currentUserId") Long currentUserId);

    @Query("""
        SELECT c.id
        FROM Chat c
        JOIN c.participants p
        WHERE c.owner.id = :userId
          AND c.isChannel = false
          AND p.id = :friendId
    """)
    List<Long> findChatsToSoftDelete(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Chat c
        SET c.deleted = true
        WHERE c.id IN :chatIds
    """)
    void softDeleteChats(@Param("chatIds") List<Long> chatIds);

}