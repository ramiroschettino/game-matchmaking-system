package com.gaming.player_service.domain.port.output;

public interface EventPublisher {
    void publishPlayerJoinedQueue(String playerId, String username, int rating);
    void publishPlayerLeftQueue(String playerId);
    void publishPlayerRatingUpdated(String playerId, int oldRating, int newRating);
}
