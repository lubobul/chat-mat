package com.chat_mat_rest_service.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantId implements java.io.Serializable {
    private Long chatId;
    private Long userId;
}
