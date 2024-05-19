package org.acme.video;

public class LikeRequest {
    public Long userId;

    // Default constructor is required for JSON deserialization
    public LikeRequest() {}

    public LikeRequest(Long userId) {
        this.userId = userId;
    }
}
