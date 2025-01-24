package com.chat_mat_rest_service.dtos.requests;

import lombok.Data;

import java.util.List;

@Data
public class CreateChatRequest {
    private String title;
    private boolean isChannel;
    private List<Long> participantIds;
}
