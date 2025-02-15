package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.Friend;
import com.chat_mat_rest_service.entities.FriendId;
import com.chat_mat_rest_service.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {

    @Query("""
    SELECT f
    FROM Friend f
    WHERE f.user.id = :userId
      AND f.friend.deleted = false
      AND (:username IS NULL OR LOWER(f.friend.username) LIKE LOWER(CONCAT('%', :username, '%')))
""")
    Page<Friend> findNonDeletedFriendsByUserId(
            @Param("userId") Long userId,
            @Param("username") String username,
            Pageable pageable
    );

    @Query("""
        SELECT f.friend.id 
        FROM Friend f 
        WHERE f.user.id = :userId
          AND f.friend.deleted = false
    """)
    List<Long> findFriendIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO friends (user_id, friend_id) VALUES (:userId, :friendId)", nativeQuery = true)
    void addFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("""
                SELECT f
                FROM Friend f
                WHERE f.user.id = :userId
                  AND f.friend.id IN :friendIds
            """)
    List<Friend> findByUserIdAndFriendIds(
            @Param("userId") Long userId,
            @Param("friendIds") List<Long> friendIds
    );

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM friends WHERE user_id = :userId AND friend_id = :friendId", nativeQuery = true)
    void removeFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    // Fetch friends not in the chat
    @Query("""
        SELECT f.friend
        FROM Friend f
        WHERE f.user.id = :userId
          AND f.friend.deleted = false
          AND f.friend.id NOT IN (
              SELECT p.user.id FROM ChatParticipant p WHERE p.chat.id = :chatId
          )
    """)
    Page<User> findFriendsNotInChat(@Param("userId") Long userId, @Param("chatId") Long chatId, Pageable pageable);

    // Fetch friends not in the chat with username filtering
    @Query("""
        SELECT f.friend
        FROM Friend f
        WHERE f.user.id = :userId
          AND f.friend.deleted = false
          AND LOWER(f.friend.username) LIKE LOWER(CONCAT('%', :usernameFilter, '%'))
          AND f.friend.id NOT IN (
              SELECT p.user.id FROM ChatParticipant p WHERE p.chat.id = :chatId
          )
    """)
    Page<User> findFriendsNotInChatFiltered(
            @Param("userId") Long userId,
            @Param("chatId") Long chatId,
            @Param("usernameFilter") String usernameFilter,
            Pageable pageable
    );

}
