package com.chat_mat_rest_service.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdminRightsRequest {
    @JsonProperty("isAdmin")
    private boolean isAdmin;
}