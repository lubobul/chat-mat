package com.chat_mat_rest_service.controllers;

import com.chat_mat_rest_service.dtos.requests.*;
import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.dtos.responses.ChatMessageDto;
import com.chat_mat_rest_service.dtos.responses.UserChatRightsDto;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.services.ChatMessageService;
import com.chat_mat_rest_service.services.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    public ChatController(
            ChatService chatService,
            ChatMessageService chatMessageService
    ) {
        this.chatService = chatService;
        this.chatMessageService = chatMessageService;
    }

    // Потребител може да създаде канал
    //Потребител може да изпрати съобщение на свои ПРИЯТЕЛ
    @Operation(
            summary = "Create or get chat",
            description = "Create a new chat or retrieve an existing chat between users"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat created or fetched successfully")
    })
    @PostMapping
    public ResponseEntity<ChatDto> getOrCreateChat(
            @RequestBody CreateChatRequest request
    ) {
        ChatDto chatResponse = chatService.getOrCreateChat(request);
        return ResponseEntity.ok(chatResponse);
    }

    //Потребителя може да види във всеки един момент всички канали в които членува, както и всички приятели, които е добавил
    @Operation(
            summary = "Get all chats",
            description = "Retrieve all chats that the current user is a participant in"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chats retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<ChatDto>> getAllChats(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "channel", required = false, defaultValue = "false") boolean isChannel,
            Pageable pageable
    ) {
        Page<ChatDto> chats = chatService.getUserChats(filter, pageable, isChannel);
        return ResponseEntity.ok(chats);
    }

    //Потребител може да чете кореспонденцията, със свой приятел
    //Потребител може да чете кореспонденцията, в произволен канал, в който членува
    @Operation(
            summary = "Get chat by ID",
            description = "Get a chat by its ID, including participants and messages"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Chat not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChatDto> getChatById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int participantsPage,
            @RequestParam(defaultValue = "25") int participantsSize,
            @RequestParam(defaultValue = "0") int messagesPage,
            @RequestParam(defaultValue = "25") int messagesSize
    ) {
        ChatDto chat = chatService.getChatById(id, participantsPage, participantsSize, messagesPage, messagesSize);
        return ResponseEntity.ok(chat);
    }

    //Потребител може да изпрати съобщение на свои ПРИЯТЕЛ
    //Потребител може да пише в произволен канал, в който членува
    @Operation(
            summary = "Send message in chat",
            description = "Send a message to a specific chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully")
    })
    @PostMapping("/{chatId}/sendMessage")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long chatId,
            @RequestBody SendMessageRequest chatMessage
    ) {
        ChatMessageDto message = chatMessageService.createMessage(chatId, chatMessage);
        return ResponseEntity.ok(message);
    }

    @Operation(
            summary = "Get chat participants",
            description = "Get the list of participants in a specific chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participants retrieved successfully")
    })
    @GetMapping("/{chatId}/participants")
    public ResponseEntity<Page<UserDto>> getChatParticipants(
            @PathVariable Long chatId,
            @RequestParam(value = "filter", required = false) String participantFilter,
            Pageable pageable
    ) {
        Page<UserDto> participants = chatService.getChatParticipants(chatId, participantFilter, pageable);
        return ResponseEntity.ok(participants);
    }

    // Потребител може да премахне ГОСТ потребител от СОБСТВЕНИЯ си канал
    // потребител с роля АДМИН на канал може да добавя нови потребители
    @Operation(
            summary = "Update chat participants",
            description = "Add or remove participants from a chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat updated successfully")
    })
    @PatchMapping("/{chatId}/participants")
    public ResponseEntity<ChatDto> updateChatParticipants(
            @PathVariable Long chatId,
            @RequestBody ParticipantsUpdateRequest participantsUpdateRequest
    ) {
        ChatDto updatedChat = chatService.updateChatParticipants(chatId, participantsUpdateRequest);
        return ResponseEntity.ok(updatedChat);
    }

    @Operation(
            summary = "Get friends not in chat",
            description = "Get the list of friends who are not part of a specific chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friends retrieved successfully")
    })
    @GetMapping("/{chatId}/friendsNotInChat")
    public ResponseEntity<Page<UserDto>> getFriendsNotInChat(
            @PathVariable Long chatId,
            @RequestParam(value = "filter", required = false) String usernameFilter,
            Pageable pageable
    ) {
        Page<UserDto> friendsNotInChat = chatService.getFriendsNotInChat(chatId, usernameFilter, pageable);
        return ResponseEntity.ok(friendsNotInChat);
    }

    @Operation(
            summary = "Get user chat rights",
            description = "Retrieve the chat rights of a user in a specific chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User chat rights retrieved successfully")
    })
    @GetMapping("/{chatId}/participants/{userId}")
    public ResponseEntity<UserChatRightsDto> getUserChatRights(
            @PathVariable Long chatId,
            @PathVariable Long userId
    ) {
        UserChatRightsDto userChatRights = chatService.getUserChatRights(chatId, userId);
        return ResponseEntity.ok(userChatRights);
    }

    // потребител с роля СОБСТВЕНИК на канал може да дава роля АДМИН на друг потребител
    @Operation(
            summary = "Update user admin status",
            description = "Assign or remove admin status for a user in a channel"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Admin status updated successfully")
    })
    @PutMapping("/{chatId}/participants/{userId}/admin")
    public ResponseEntity<Void> updateAdminStatus(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @RequestBody AdminRightsRequest requestBody
    ) {
        chatService.updateAdminStatus(chatId, userId, requestBody);
        return ResponseEntity.noContent().build();
    }

    // потребител с роля ГОСТ на канал НЕ може да променя името на канал
    @Operation(
            summary = "Update chat",
            description = "Update chat details like name, description, etc."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat updated successfully")
    })
    @PutMapping("/{chatId}")
    public ResponseEntity<ChatDto> updateChat(
            @PathVariable Long chatId,
            @RequestBody UpdateChatRequest updateChatRequest
    ) {
        ChatDto updatedChat = chatService.updateChat(chatId, updateChatRequest);
        return ResponseEntity.ok(updatedChat);
    }

    //потребител може да изтрие СОБСТВЕНИЯ си канал
    //потребител с роля ГОСТ на канал НЕ може да изтрива канал
    @Operation(
            summary = "Delete a chat",
            description = "Soft delete a chat if the current user is the owner"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this chat")
    })
    @DeleteMapping("/{chatId}")
    public ResponseEntity<ChatDto> deleteChat(
            @PathVariable Long chatId
    ) {
        ChatDto updatedChat = chatService.softDeleteChat(chatId);
        return ResponseEntity.ok(updatedChat);
    }

    @Operation(
            summary = "Leave chat",
            description = "Allow a user to leave a chat"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User left the chat successfully")
    })
    @PutMapping("/{chatId}/leave")
    public ResponseEntity<Void> leaveChat(@PathVariable Long chatId) {
        chatService.leaveChat(chatId);
        return ResponseEntity.noContent().build();
    }
}
