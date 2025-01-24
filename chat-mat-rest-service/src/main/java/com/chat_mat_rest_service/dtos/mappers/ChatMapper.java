package com.chat_mat_rest_service.dtos.mappers;

import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.entities.Chat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMapper extends GenericMapper<Chat, ChatDto> {
}
