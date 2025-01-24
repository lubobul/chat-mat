package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.dtos.requests.CreateChatRequest;
import com.chat_mat_rest_service.services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatDto> createChat(
            @RequestBody CreateChatRequest request
    ) {
        ChatDto chatResponse = chatService.createChat(request);
        return ResponseEntity.ok(chatResponse);
    }
}
