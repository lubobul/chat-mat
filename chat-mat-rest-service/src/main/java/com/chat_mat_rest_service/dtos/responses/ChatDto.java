package com.chat_mat_rest_service.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.sql.Timestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long id;
    private String title;
    private Boolean isChannel;
    private Timestamp createdAt;
    private UserDto owner;
    private Page<UserDto> participantsPage;
    private Page<ChatMessageDto> messagesPage;
}
