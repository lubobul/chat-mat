package com.chat_mat_rest_service.dtos.requests;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String messageContent;
}