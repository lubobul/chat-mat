package com.chat_mat_rest_service.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatRequest {
    private String title;
    @JsonProperty("isChannel")
    private boolean isChannel;
    private List<Long> participantIds;
}
