package com.chat_mat_rest_service.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class FriendId implements Serializable {
    private Long userId;
    private Long friendId;
}
