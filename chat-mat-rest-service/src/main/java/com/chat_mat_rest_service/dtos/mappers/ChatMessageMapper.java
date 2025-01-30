package com.chat_mat_rest_service.dtos.mappers;
import com.chat_mat_rest_service.dtos.responses.ChatMessageDto;
import com.chat_mat_rest_service.entities.ChatMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper extends GenericMapper<ChatMessage, ChatMessageDto> {

    @Override
    default ChatMessageDto toDto(ChatMessage chatMessage){
        return new ChatMessageDto(
                chatMessage.getId(),
                chatMessage.getMessage(),
                chatMessage.getSender().getId(),
                chatMessage.getSender().getUsername(),
                chatMessage.getCreatedAt()
        );
    }
}
