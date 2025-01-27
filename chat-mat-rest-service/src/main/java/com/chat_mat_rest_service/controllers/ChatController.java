package com.chat_mat_rest_service.controllers;

import com.chat_mat_rest_service.dtos.requests.SendMessageRequest;
import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.dtos.requests.CreateChatRequest;
import com.chat_mat_rest_service.dtos.responses.ChatMessageDto;
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
        ChatDto chatResponse = chatService.createChat(request);
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
}
