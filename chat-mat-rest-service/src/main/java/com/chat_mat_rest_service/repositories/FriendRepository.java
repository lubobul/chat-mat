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

public interface FriendRepository extends JpaRepository<Friend, FriendId> {

    // Find friends of a user with optional pagination
    Page<Friend> findByUserId(Long userId, Pageable pageable);

    // Filter friends by username
    Page<Friend> findByUserIdAndFriendUsernameContainingIgnoreCase(Long userId, String username, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO friends (user_id, friend_id) VALUES (:userId, :friendId)", nativeQuery = true)
    void addFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
