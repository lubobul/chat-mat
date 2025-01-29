package com.chat_mat_rest_service.controllers;

import com.chat_mat_rest_service.dtos.requests.ParticipantsUpdateRequest;
import com.chat_mat_rest_service.dtos.requests.SendMessageRequest;
import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.dtos.requests.CreateChatRequest;
import com.chat_mat_rest_service.dtos.responses.ChatMessageDto;
import com.chat_mat_rest_service.dtos.responses.UserChatRightsDto;
import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.services.ChatMessageService;
import com.chat_mat_rest_service.services.ChatService;
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

    @PostMapping
    public ResponseEntity<ChatDto> getOrCreateChat(
            @RequestBody CreateChatRequest request
    ) {
        ChatDto chatResponse = chatService.getOrCreateChat(request);
        return ResponseEntity.ok(chatResponse);
    }

    @GetMapping
    public ResponseEntity<Page<ChatDto>> getAllChats(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "channel", required = false, defaultValue = "false") boolean isChannel,
            Pageable pageable
    ) {
        Page<ChatDto> chats = chatService.getUserChats(filter, pageable, isChannel);
        return ResponseEntity.ok(chats);
    }

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

    @PostMapping("/{chatId}/sendMessage")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long chatId,
            @RequestBody SendMessageRequest chatMessage
    ) {
        ChatMessageDto message = chatMessageService.createMessage(chatId, chatMessage);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{chatId}/participants")
    public ResponseEntity<Page<UserDto>> getChatParticipants(
            @PathVariable Long chatId,
            @RequestParam(value = "filter", required = false) String participantFilter,
            Pageable pageable
    ) {
        Page<UserDto> participants = chatService.getChatParticipants(chatId, participantFilter, pageable);
        return ResponseEntity.ok(participants);
    }

    @PatchMapping("/{chatId}/participants")
    public ResponseEntity<ChatDto> updateChatParticipants(
            @PathVariable Long chatId,
            @RequestBody ParticipantsUpdateRequest participantsUpdateRequest
    ) {
        ChatDto updatedChat = chatService.updateChatParticipants(chatId, participantsUpdateRequest);
        return ResponseEntity.ok(updatedChat);
    }


    @GetMapping("/{chatId}/friendsNotInChat")
    public ResponseEntity<Page<UserDto>> getFriendsNotInChat(
            @PathVariable Long chatId,
            @RequestParam(value = "filter", required = false) String usernameFilter,
            Pageable pageable

    ) {
        Page<UserDto> friendsNotInChat = chatService.getFriendsNotInChat(chatId, usernameFilter, pageable);
        return ResponseEntity.ok(friendsNotInChat);
    }

    @GetMapping("/{chatId}/participants/{userId}")
    public ResponseEntity<UserChatRightsDto> getUserChatRights(
            @PathVariable Long chatId,
            @PathVariable Long userId
    ) {
        UserChatRightsDto userChatRights = chatService.getUserChatRights(chatId, userId);
        return ResponseEntity.ok(userChatRights);
    }

}
