package com.chat_mat_rest_service.dtos.rest;

import lombok.Data;

import java.util.List;

@Data
public class CreateChatRequest {
    private String title;
    private List<Long> participantIds;
}
