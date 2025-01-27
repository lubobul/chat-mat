package com.chat_mat_rest_service.dtos.requests;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
    private String avatar;  // Base64 encoded image
    // Add other fields you want to allow the user to update
}