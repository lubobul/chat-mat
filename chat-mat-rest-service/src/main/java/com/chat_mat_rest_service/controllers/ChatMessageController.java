package com.chat_mat_rest_service.controllers;

import com.chat_mat_rest_service.services.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Operation(
            summary = "Delete a message",
            description = "Soft deletes a message identified by the messageId, if the current user is the sender."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Message not found"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this message")
    })
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable
                                              @Parameter(description = "ID of the message to delete") Long messageId) {
        chatMessageService.softDeleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
