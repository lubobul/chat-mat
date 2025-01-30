package com.chat_mat_rest_service.services;

import com.chat_mat_rest_service.custom.exceptions.ResourceNotFoundException;
import com.chat_mat_rest_service.custom.exceptions.UnauthorizedException;
import com.chat_mat_rest_service.dtos.mappers.ChatMessageMapper;
import com.chat_mat_rest_service.dtos.mappers.UserMapper;
import com.chat_mat_rest_service.dtos.requests.AdminRightsRequest;
import com.chat_mat_rest_service.dtos.requests.ParticipantsUpdateRequest;
import com.chat_mat_rest_service.dtos.requests.UpdateChatRequest;
import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.dtos.mappers.ChatMapper;
import com.chat_mat_rest_service.dtos.requests.CreateChatRequest;
import com.chat_mat_rest_service.dtos.responses.ChatUserType;
import com.chat_mat_rest_service.dtos.responses.UserChatRightsDto;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.entities.*;
import com.chat_mat_rest_service.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.chat_mat_rest_service.common.SecurityContextHelper.getAuthenticatedUserId;


@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMapper chatMapper;
    private final UserMapper userMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final FriendRepository friendRepository;
    public ChatService(
            ChatRepository chatRepository,
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository,
            ChatParticipantRepository chatParticipantRepository,
            ChatMapper chatMapper,
            UserMapper userMapper,
            ChatMessageMapper chatMessageMapper,
            FriendRepository friendRepository
    ) {
        this.chatRepository = chatRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatMapper = chatMapper;
        this.userMapper = userMapper;
        this.chatMessageMapper = chatMessageMapper;
        this.friendRepository = friendRepository;
    }

    public ChatDto getChatById(
            Long id,
            int participantsPage,
            int participantsSize,
            int messagesPage,
            int messagesSize
    ) {
        Long currentUserId = getAuthenticatedUserId();

        // Fetch the basic chat details
        Chat chat = chatRepository.findByIdAndOwnerIdOrParticipantId(id, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        // Fetch paginated participants
        Pageable participantsPageable = PageRequest.of(participantsPage, participantsSize);
        Page<User> participants = chatParticipantRepository.findParticipantUsersByChatId(chat.getId(), participantsPageable);

        // Fetch paginated messages
        Pageable messagesPageable = PageRequest.of(messagesPage, messagesSize);
        Page<ChatMessage> messages = chatMessageRepository.findMessagesByChatId(chat.getId(), messagesPageable);

        // Reverse the messages content
        List<ChatMessage> reversedMessages = new ArrayList<>(messages.getContent());
        Collections.reverse(reversedMessages);

        // Create a new PageImpl with reversed content
        Page<ChatMessage> reversedMessagesPage = new PageImpl<>(
                reversedMessages,
                messages.getPageable(),
                messages.getTotalElements()
        );

        // Map to DTO
        ChatDto chatDto = chatMapper.toDto(chat);
        chatDto.setOwner(userMapper.toDto(chat.getOwner()));
        chatDto.setParticipantsPage(participants.map(userMapper::toDto));
        chatDto.setMessagesPage(reversedMessagesPage.map(chatMessageMapper::toDto));

        return chatDto;
    }

    //Make sure chat can be created for only friends belonging to current user
    public ChatDto getOrCreateChat(CreateChatRequest request) {
        Long currentUserId = getAuthenticatedUserId(); // Use your existing method to extract userId from JWT

        // Fetch the current user (chat owner)
        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Fetch the current user's friend IDs
        List<Long> friendIds = friendRepository.findFriendIdsByUserId(currentUserId);

        // Validate participants
        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new IllegalArgumentException("Participants list cannot be empty");
        }

        // Ensure that all participants are from the current user's friend list
        List<Long> invalidParticipants = new ArrayList<>();
        for (Long participantId : request.getParticipantIds()) {
            if (!friendIds.contains(participantId)) {
                invalidParticipants.add(participantId);
            }
        }

        if (!invalidParticipants.isEmpty()) {
            throw new IllegalArgumentException("The following participants are not in your friend list: " + invalidParticipants);
        }

        // Check for existing non-channel chat with the participant
        Long firstParticipantId = request.getParticipantIds().getFirst();
        if (!request.isChannel()) {
            List<Chat> existingChats = chatRepository.findNonChannelChatsByParticipantsOrOwner(currentUserId, firstParticipantId);
            if (!existingChats.isEmpty()) {
                return chatMapper.toDto(existingChats.getFirst()); // Return the first matching chat
            }
        }

        // Fetch participants from the database
        List<User> participants = userRepository.findAllById(request.getParticipantIds());
        if (participants.isEmpty()) {
            throw new ResourceNotFoundException("Participants not found");
        }

        // Add the owner to the participants list
        participants.add(owner);

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

    public Page<ChatDto> getUserChats(String filter, Pageable pageable, boolean isChannel) {
        Long currentUserId = getAuthenticatedUserId(); // Fetch the current user's ID

        Page<Chat> chats;
        if (filter != null && !filter.isEmpty()) {
            // Parse the filter for chatTitle==value
            String[] conditions = filter.split(",");
            String chatTitle = null;

            for (String condition : conditions) {
                if (condition.startsWith("title==")) {
                    chatTitle = condition.substring("title==".length());
                    break;
                }
            }

            if (chatTitle != null) {
                // Fetch chats where the user is either the owner or a participant with the given title
                chats = chatRepository.findByOwnerIdOrParticipantIdAndTitleContainingIgnoreCase(
                        currentUserId, chatTitle, isChannel, pageable
                );
            } else {
                chats = chatRepository.findByOwnerIdOrParticipantId(currentUserId, isChannel, pageable);
            }
        } else {
            // Fetch all chats where the user is either the owner or a participant
            chats = chatRepository.findByOwnerIdOrParticipantId(currentUserId, isChannel, pageable);
        }

        // Map Chat entities to ChatDto
        return chats.map(chat -> {
            ChatDto chatDto = chatMapper.toDto(chat);

            // Only add two participants if the chat is not a channel
            if (!chat.getIsChannel()) {

                // Add only one participant to participantsPage if participants exist
                if (chat.getParticipants() != null && !chat.getParticipants().isEmpty()) {
                    List<UserDto> singleParticipantList = chat.getParticipants().stream()
                            .limit(2)
                            .map(userMapper::toDto)
                            .toList();
                    chatDto.setParticipantsPage(new PageImpl<>(singleParticipantList));
                }
            }

            return chatDto;
        });
    }

    public Page<UserDto> getChatParticipants(Long chatId, String filter, Pageable pageable) {
        Long currentUserId = getAuthenticatedUserId();

        // Ensure user has access to chat participants
        Chat chat = chatRepository.findByIdAndOwnerIdOrParticipantId(chatId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("You are not allowed to view participants of this chat."));

        String username = null;
        if (filter != null) {
            for (String condition : filter.split(",")) {
                if (condition.startsWith("username==")) {
                    username = condition.substring("username==".length());
                }
            }
        }

        // Fetch paginated participants with optional filtering
        Page<ChatParticipant> participants;
        if (username != null && !username.isEmpty()) {
            participants = chatParticipantRepository.findParticipantsByChatIdAndUsernameContainingIgnoreCase(chatId, username, pageable);
        } else {
            participants = chatParticipantRepository.findParticipantsByChatId(chatId, pageable);
        }

        // Get friend list for the current user
        List<Long> friendIds = friendRepository.findFriendIdsByUserId(currentUserId);

        // Map participants to DTOs with isFriendOfYours and chatUserType
        return participants.map(participant -> {
            User user = participant.getUser();
            UserDto userDto = userMapper.toDto(user);

            userDto.setIsFriendOfYours(friendIds.contains(user.getId()));

            // Determine chatUserType
            if (chat.getOwner().getId().equals(user.getId())) {
                userDto.setChatUserType(ChatUserType.OWNER);
            } else if (participant.getIsAdmin()) {
                userDto.setChatUserType(ChatUserType.ADMIN);
            } else {
                userDto.setChatUserType(ChatUserType.PARTICIPANT);
            }

            return userDto;
        });
    }

    public Page<UserDto> getFriendsNotInChat(Long chatId, String filter, Pageable pageable) {
        Long currentUserId = getAuthenticatedUserId();

        // Ensure user is owner of the chat
        boolean isAuthorized = chatRepository.existsByIdAndOwnerIdOrAdminId(chatId, currentUserId);
        if (!isAuthorized) {
            throw new ResourceNotFoundException("You are not allowed to view this chat.");
        }
        String username = null;

        if (filter != null) {
            // Split filter into individual conditions
            String[] conditions = filter.split(",");
            for (String condition : conditions) {
                if (condition.startsWith("username==")) {
                    username = condition.substring("username==".length());
                }
            }
        }

        // Get friends of the current user
        Page<User> friends;
        if (username != null && !username.isEmpty()) {
            friends = friendRepository.findFriendsNotInChatFiltered(currentUserId, chatId, username, pageable);
        } else {
            friends = friendRepository.findFriendsNotInChat(currentUserId, chatId, pageable);
        }

        // Map to DTO
        return friends.map(userMapper::toDto);
    }

    //For now only verifications are:
    //To update chat participants current user must be: OWNER | ADMIN
    //The removedParticipants list cannot contain: OWNER
    //Currently the ADMIN can remove another ADMIN
    @Transactional
    public ChatDto updateChatParticipants(Long chatId, ParticipantsUpdateRequest request) {
        Long currentUserId = getAuthenticatedUserId();

        // Ensure the user is authorized (must be chat owner or admin)
        Chat chat = chatRepository.findByIdAndOwnerIdOrAdminId(chatId, currentUserId)
                .orElseThrow(() -> new UnauthorizedException("Access denied"));

        // Fetch the chat owner
        Long chatOwnerId = chat.getOwner().getId();

        // Fetch users to be added
        List<User> usersToAdd = request.getAddedParticipants() != null
                ? userRepository.findAllById(request.getAddedParticipants())
                : Collections.emptyList();

        // Fetch users to be removed and ensure the chat owner is not removed
        List<User> usersToRemove = request.getRemovedParticipants() != null
                ? userRepository.findAllById(request.getRemovedParticipants())
                : Collections.emptyList();

        // Perhaps we can throw UnauthorizedException here.
        // Remove the chat owner from the removal list if present
        usersToRemove.removeIf(user -> user.getId().equals(chatOwnerId));

        // Remove participants
        usersToRemove.forEach(user -> {
            chatParticipantRepository.deleteByChatIdAndUserId(chat.getId(), user.getId());
        });

        // Add new participants
        usersToAdd.forEach(user -> {
            if (!chat.getParticipants().contains(user)) {
                ChatParticipant chatParticipant = new ChatParticipant();
                chatParticipant.setId(new ChatParticipantId(chat.getId(), user.getId()));
                chatParticipant.setChat(chat);
                chatParticipant.setUser(user);
                chatParticipant.setIsAdmin(false); // Default to non-admin
                chatParticipantRepository.save(chatParticipant);
            }
        });

        return chatMapper.toDto(chat);
    }


    public UserChatRightsDto getUserChatRights(Long chatId, Long userId) {
        Long currentUserId = getAuthenticatedUserId(); // Ensure caller has access

        // Check if the chat exists and the current user has access
        chatRepository.findByIdAndOwnerIdOrParticipantId(chatId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("You are not allowed to view this chat."));

        // Fetch user details
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Determine Chat Role
        ChatUserType chatUserType;
        if (chatRepository.existsByIdAndOwnerId(chatId, userId)) {
            chatUserType = ChatUserType.OWNER;
        } else if (chatParticipantRepository.isUserAdminInChat(chatId, userId)) {
            chatUserType = ChatUserType.ADMIN;
        } else {
            chatUserType = ChatUserType.PARTICIPANT;
        }

        // Map to DTO
        return new UserChatRightsDto(userMapper.toDto(user), chatId, chatUserType);
    }

    public void updateAdminStatus(Long chatId, Long userId, AdminRightsRequest requestBody) {
        Long currentUserId = getAuthenticatedUserId(); // Get requester ID

        // Ensure the requester is the chat owner
        boolean isOwner = chatRepository.existsByIdAndOwnerId(chatId, currentUserId);
        if(!isOwner){
            throw new UnauthorizedException("Only the chat owner can modify admin roles");
        }

        // Ensure the target user is a participant
        boolean isParticipant = chatParticipantRepository.existsByChatIdAndUserId(chatId, userId);
        if (!isParticipant) {
            throw new ResourceNotFoundException("User is not a participant in this chat");
        }

        // Update the admin status
        chatParticipantRepository.updateAdminStatus(chatId, userId, requestBody.isAdmin());
    }

    @Transactional
    public ChatDto updateChat(Long chatId, UpdateChatRequest updateRequest) {
        Long currentUserId = getAuthenticatedUserId(); // Get requester ID

        // потребител с роля ГОСТ на канал НЕ може да променя името на канал
        // Check if the user is either an owner or admin of the chat
        boolean hasEditRights = chatRepository.existsByIdAndOwnerIdOrAdminId(chatId, currentUserId);
        if (!hasEditRights) {
            throw new UnauthorizedException("You don't have the rights to edit this chat.");
        }

        // Retrieve the chat entity
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        // Update the chat title
        chat.setTitle(updateRequest.getChatTitle());

        // Save the updated chat
        return chatMapper.toDto(chatRepository.save(chat));
    }

    @Transactional
    public ChatDto softDeleteChat(Long chatId) {
        Long currentUserId = getAuthenticatedUserId();  // Get the current user ID

        // Check if the user is either the owner or an admin of the chat
        //потребител с роля ГОСТ на канал НЕ може да изтрива канал
        boolean hasDeleteRights = chatRepository.existsByIdAndOwnerId(chatId, currentUserId);
        if (!hasDeleteRights) {
            throw new UnauthorizedException("You don't have the rights to delete this chat.");
        }

        // Retrieve the chat entity
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        // Soft delete the chat by setting the 'deleted' flag to true
        chat.setDeleted(true);

        // Save the updated chat
        chat = chatRepository.save(chat);

        // Return the updated chat as a DTO
        return chatMapper.toDto(chat);
    }

    @Transactional
    public void leaveChat(Long chatId) {
        Long currentUserId = getAuthenticatedUserId(); // Fetch the current user's ID

        // Ensure the user is part of the chat
        ChatParticipant chatParticipant = chatParticipantRepository.findByChatIdAndUserId(chatId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a participant in this chat"));

        // Remove the user from the chat participants
        chatParticipantRepository.delete(chatParticipant);
    }
}
