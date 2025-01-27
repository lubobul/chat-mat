package com.chat_mat_rest_service.repositories;
import com.chat_mat_rest_service.entities.ChatParticipant;
import com.chat_mat_rest_service.entities.ChatParticipantId;
import com.chat_mat_rest_service.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, ChatParticipantId> {
    @Query("""
    SELECT p.user 
    FROM ChatParticipant p 
    WHERE p.chat.id = :chatId
""")
    Page<User> findParticipantsByChatId(@Param("chatId") Long chatId, Pageable pageable);

}