package com.chat_mat_rest_service.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "friends")
@Data
public class Friend {

    @EmbeddedId
    private FriendId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("friendId")
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;
}
