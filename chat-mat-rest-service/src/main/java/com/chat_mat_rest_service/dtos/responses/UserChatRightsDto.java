package com.chat_mat_rest_service.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChatRightsDto {
    private UserDto user;
    private Long chatId;
    private ChatUserType chatUserType;
}