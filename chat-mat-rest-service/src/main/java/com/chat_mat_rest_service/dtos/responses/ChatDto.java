package com.chat_mat_rest_service.dtos.responses;

import com.chat_mat_rest_service.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long id;
    private String title;
    private Boolean isChannel;
    private Timestamp createdAt;
    private UserDto owner;
    private List<UserDto> participants;
    private List<ChatMessageDto> messages;
    private Pageable participantsPageDetails;
    private Pageable messagesPageDetails;
}
