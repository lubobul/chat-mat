package com.chat_mat_rest_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "chat_participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipant {

    @EmbeddedId
    private ChatParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatId")
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @Column(nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "joined_at", nullable = false, updatable = false, insertable = false)
    private Timestamp joinedAt;
}

