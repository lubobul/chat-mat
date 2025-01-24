package com.chat_mat_rest_service.dtos.mappers;

import com.chat_mat_rest_service.dtos.entities.ChatDto;
import com.chat_mat_rest_service.dtos.entities.UserDto;
import com.chat_mat_rest_service.entities.Chat;
import com.chat_mat_rest_service.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper extends GenericMapper<Chat, ChatDto> {
}
