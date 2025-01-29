package com.chat_mat_rest_service.repositories;

import com.chat_mat_rest_service.entities.Friend;
import com.chat_mat_rest_service.entities.FriendId;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, FriendId> {

    // Find friends of a user with optional pagination
    Page<Friend> findByUserId(Long userId, Pageable pageable);

    // Filter friends by username
    Page<Friend> findByUserIdAndFriendUsernameContainingIgnoreCase(Long userId, String username, Pageable pageable);

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

}
