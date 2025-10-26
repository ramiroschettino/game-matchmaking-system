package com.gaming.player_service.infrastructure.adapter.output.messaging;

import com.gaming.player_service.domain.port.output.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQEventPublisher implements EventPublisher {

    @Override
    public void publishPlayerJoinedQueue(String playerId, String username, int rating) {
        log.info("EVENT: Player {} ({}) joined queue with rating {}", playerId, username, rating);
        // TODO: Implement RabbitMQ publishing
    }

    @Override
    public void publishPlayerLeftQueue(String playerId) {
        log.info("EVENT: Player {} left queue", playerId);
        // TODO: Implement RabbitMQ publishing
    }

    @Override
    public void publishPlayerRatingUpdated(String playerId, int oldRating, int newRating) {
        log.info("EVENT: Player {} rating updated from {} to {}", playerId, oldRating, newRating);
        // TODO: Implement RabbitMQ publishing
    }
}