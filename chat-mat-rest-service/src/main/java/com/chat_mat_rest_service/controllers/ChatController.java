package com.chat_mat_rest_service.controllers;
import com.chat_mat_rest_service.dtos.responses.ChatDto;
import com.chat_mat_rest_service.dtos.requests.CreateChatRequest;
import com.chat_mat_rest_service.services.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public ResponseEntity<Page<ChatDto>> getAllChats(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "channel", required = false, defaultValue = "false") boolean isChannel,
            Pageable pageable
    ) {
        Page<ChatDto> chats = chatService.getUserChats(filter, pageable, isChannel);
        return ResponseEntity.ok(chats);
    }
}
