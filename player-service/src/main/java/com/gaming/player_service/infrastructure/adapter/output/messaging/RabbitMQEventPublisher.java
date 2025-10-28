package com.gaming.player_service.infrastructure.adapter.output.messaging;

import com.gaming.player_service.domain.port.output.EventPublisher;
import com.gaming.player_service.infrastructure.adapter.output.messaging.dto.PlayerJoinedQueueEvent;
import com.gaming.player_service.infrastructure.adapter.output.messaging.dto.PlayerLeftQueueEvent;
import com.gaming.player_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishPlayerJoinedQueue(String playerId, String username, int rating) {
        log.info("Publishing PlayerJoinedQueue event: Player {} with rating {}", playerId, rating);

        PlayerJoinedQueueEvent event = PlayerJoinedQueueEvent.builder()
                .playerId(playerId)
                .username(username)
                .rating(rating)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.GAME_EVENTS_EXCHANGE,
                RabbitMQConfig.PLAYER_JOINED_QUEUE_KEY,
                event
        );

        log.info("PlayerJoinedQueue event published successfully");
    }

    @Override
    public void publishPlayerLeftQueue(String playerId) {
        log.info("Publishing PlayerLeftQueue event: Player {}", playerId);

        PlayerLeftQueueEvent event = PlayerLeftQueueEvent.builder()
                .playerId(playerId)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.GAME_EVENTS_EXCHANGE,
                RabbitMQConfig.PLAYER_LEFT_QUEUE_KEY,
                event
        );

        log.info("PlayerLeftQueue event published successfully");
    }

    @Override
    public void publishPlayerRatingUpdated(String playerId, int oldRating, int newRating) {
        log.info("Publishing PlayerRatingUpdated event: Player {} from {} to {}",
                playerId, oldRating, newRating);
        // Este evento no lo consume Match Service por ahora
    }
}