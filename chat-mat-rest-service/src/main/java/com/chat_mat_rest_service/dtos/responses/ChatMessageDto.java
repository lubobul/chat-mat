package com.chat_mat_rest_service.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String messageContent;
    private Long senderId;
    private String senderUsername;
    private Timestamp createdAt;
    private boolean isDeleted;
}