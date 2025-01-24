package com.chat_mat_rest_service.common;

import com.chat_mat_rest_service.auth.JwtUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {
    public static Long getAuthenticatedUserId() {
        JwtUserDetails userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUserId();
    }
}
