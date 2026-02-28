package com.oceanview.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String userId;

    public AuthResponse(String token, String username, String role, String userId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getUserId() { return userId; }
}
