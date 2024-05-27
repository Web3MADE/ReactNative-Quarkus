package org.acme.user;

public class UserResponse {
    private String token;
    private Long userId;

    public UserResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
