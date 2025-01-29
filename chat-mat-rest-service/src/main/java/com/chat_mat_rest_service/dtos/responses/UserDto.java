package com.chat_mat_rest_service.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String avatar; // Base64 encoded image
    private Timestamp createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isFriendOfYours; // Will be absent if null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ChatUserType chatUserType;
}
