package com.chat_mat_rest_service.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class ChatDto {
    private Long id;
    private String title;
    private Boolean isChannel;
    private Timestamp createdAt;
}
