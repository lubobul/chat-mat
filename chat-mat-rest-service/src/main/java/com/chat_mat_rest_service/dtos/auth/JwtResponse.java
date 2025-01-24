package com.chat_mat_rest_service.dtos.auth;

import com.chat_mat_rest_service.dtos.responses.UserDto;
import com.chat_mat_rest_service.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    UserDto user;
    private String token;
}
