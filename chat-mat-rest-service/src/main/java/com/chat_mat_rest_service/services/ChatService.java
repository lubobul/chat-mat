package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.dtos.mappers.ChatMapper;
import com.chat_mat_rest_service.dtos.requests.CreateChatRequest;
import com.chat_mat_rest_service.entities.Chat;
import com.chat_mat_rest_service.entities.ChatParticipant;
import com.chat_mat_rest_service.entities.ChatParticipantId;
import com.chat_mat_rest_service.entities.User;
import com.chat_mat_rest_service.repositories.ChatParticipantRepository;
import com.chat_mat_rest_service.repositories.ChatRepository;
import com.chat_mat_rest_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.chat_mat_rest_service.common.SecurityContextHelper.getAuthenticatedUserId;


@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMapper chatMapper;

    public ChatService(
            ChatRepository chatRepository,
            UserRepository userRepository,
            ChatParticipantRepository chatParticipantRepository,
            ChatMapper chatMapper
    ) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatMapper = chatMapper;
    }

    public ChatDto createChat(CreateChatRequest request) {
        Long currentUserId = getAuthenticatedUserId(); // Use your existing method to extract userId from JWT

        // Fetch the current user (chat owner)
        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate participants
        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Participants list cannot be empty");
        }

        Long firstParticipantId = request.getParticipantIds().getFirst();

        // Check for existing non-channel chat with the participant
        if (!request.isChannel()) {
            List<Chat> existingChats = chatRepository.findNonChannelChatsByParticipants(currentUserId, firstParticipantId);
            if (!existingChats.isEmpty()) {
                return chatMapper.toDto(existingChats.getFirst()); // Return the first matching chat
            }
        }

        // Fetch participants
        List<User> participants = userRepository.findAllById(request.getParticipantIds());
        if (participants.isEmpty()) {
            throw new IllegalArgumentException("Participants not found");
        }

        // Create and save the new chat
        Chat chat = new Chat();
        chat.setTitle(request.getTitle());
        chat.setIsChannel(request.isChannel());
        chat.setOwner(owner);
        chat.setParticipants(participants);
        chatRepository.save(chat);

        // Add participants to the chat_participants table
        participants.forEach(participant -> {
            ChatParticipant chatParticipant = new ChatParticipant();
            chatParticipant.setId(new ChatParticipantId(chat.getId(), participant.getId()));
            chatParticipant.setChat(chat);
            chatParticipant.setUser(participant);
            chatParticipant.setIsAdmin(false); // Default to non-admin
            chatParticipantRepository.save(chatParticipant);
        });

        return chatMapper.toDto(chat);
    }
}
