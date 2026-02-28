package com.oceanview.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Carries userId from the JWT so controllers can resolve the current user id
 * without a DB lookup (avoids "Not allowed to update/delete" when username lookup fails).
 */
public class JwtAuthDetails extends WebAuthenticationDetails {
    private final String userId;

    public JwtAuthDetails(HttpServletRequest request, String userId) {
        super(request);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
