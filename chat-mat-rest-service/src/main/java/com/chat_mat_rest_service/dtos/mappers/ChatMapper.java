package com.chat_mat_rest_service.dtos.mappers;

import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.dtos.responses.ChatMessageDto;
import com.chat_mat_rest_service.entities.Chat;
import com.chat_mat_rest_service.entities.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper extends GenericMapper<Chat, ChatDto> {
    ChatDto chatDto = new ChatDto();
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "isChannel", source = "isChannel")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "owner", ignore = true) // Ignore this field
    @Mapping(target = "participants", ignore = true) // Ignore this field
    @Mapping(target = "messages", ignore = true) // Ignore this field
    ChatDto toDto(Chat chat);
}
