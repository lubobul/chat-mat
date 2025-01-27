package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.custom.exceptions.ResourceNotFoundException;
import com.chat_mat_rest_service.dtos.mappers.ChatMessageMapper;
import com.chat_mat_rest_service.dtos.requests.SendMessageRequest;
import com.chat_mat_rest_service.dtos.responses.ChatMessageDto;
import com.chat_mat_rest_service.entities.Chat;
import com.chat_mat_rest_service.entities.ChatMessage;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.repositories.ChatMessageRepository;
import com.chat_mat_rest_service.repositories.ChatRepository;
import com.chat_mat_rest_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import static com.chat_mat_rest_service.common.SecurityContextHelper.getAuthenticatedUserId;
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public ChatMessageService(
            ChatMessageRepository chatMessageRepository,
            ChatRepository chatRepository,
            ChatMessageMapper chatMessageMapper,
            UserRepository userRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    public ChatMessageDto createMessage(Long chatId, SendMessageRequest chatMessage) {
        Long userId = getAuthenticatedUserId();
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));

        Chat chat = chatRepository.findByIdAndOwnerIdOrParticipantId(chatId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found."));

        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setSender(sender);
        message.setMessage(chatMessage.getMessageContent());

        chatMessageRepository.save(message);

        return chatMessageMapper.toDto(message);
    }
}
