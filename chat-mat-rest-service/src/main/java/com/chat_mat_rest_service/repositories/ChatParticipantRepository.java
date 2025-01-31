package com.chat_mat_rest_service.repositories;
import com.chat_mat_rest_service.entities.ChatParticipant;
import com.chat_mat_rest_service.entities.ChatParticipantId;
import com.chat_mat_rest_service.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
    @Query("""
    SELECT p 
    FROM ChatParticipant p 
    WHERE p.chat.id = :chatId AND p.user.deleted = false
""")
    Page<ChatParticipant> findParticipantsByChatId(@Param("chatId") Long chatId, Pageable pageable);

    @Query("""
    SELECT p.user 
    FROM ChatParticipant p 
    WHERE p.chat.id = :chatId
""")
    Page<User> findParticipantUsersByChatId(@Param("chatId") Long chatId, Pageable pageable);

    @Query("""
    SELECT p 
    FROM ChatParticipant p 
    WHERE p.chat.id = :chatId AND p.user.deleted = false
    AND LOWER(p.user.username) LIKE LOWER(CONCAT('%', :username, '%'))
""")
    Page<ChatParticipant> findParticipantsByChatIdAndUsernameContainingIgnoreCase(
            @Param("chatId") Long chatId,
            @Param("username") String username,
            Pageable pageable
    );

    @Modifying
    @Transactional
    void deleteByChatIdAndUserId(Long chatId, Long userId);

    @Query("""
    SELECT p.isAdmin 
    FROM ChatParticipant p 
    WHERE p.chat.id = :chatId AND p.user.id = :userId
""")
    Boolean isUserAdminInChat(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Modifying
    @Query("""
    UPDATE ChatParticipant p 
    SET p.isAdmin = :isAdmin 
    WHERE p.chat.id = :chatId AND p.user.id = :userId
""")
    @Transactional
    void updateAdminStatus(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("isAdmin") boolean isAdmin);

    boolean existsByChatIdAndUserId(Long chatId, Long userId);

    Optional<ChatParticipant> findByChatIdAndUserId(Number chatId, Number currentUserId);

}