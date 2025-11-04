package com.gaming.match_service.infrastructure.adapter.input.messaging;

import com.gaming.match_service.domain.service.MatchmakingService;
import com.gaming.match_service.infrastructure.adapter.input.messaging.dto.PlayerJoinedQueueEvent;
import com.gaming.match_service.infrastructure.adapter.input.messaging.dto.PlayerLeftQueueEvent;
import com.gaming.match_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerQueueListener {

    private final MatchmakingService matchmakingService;

    @RabbitListener(queues = RabbitMQConfig.MATCHMAKING_QUEUE)
    public void handlePlayerJoinedQueue(PlayerJoinedQueueEvent event) {
        log.info("RABBITMQ: Received PlayerJoinedQueue event - Player: {}, Rating: {}",
                event.getPlayerId(), event.getRating());

        try {
            matchmakingService.addPlayerToQueue(
                    event.getPlayerId(),
                    event.getUsername(),
                    event.getRating()
            );
        } catch (Exception e) {
            log.error("Error processing PlayerJoinedQueue event", e);
        }
    }

    // ⚠️ COMENTAR O BORRAR ESTE MÉTODO (está causando el problema)
    /*
    @RabbitListener(queues = RabbitMQConfig.MATCHMAKING_QUEUE)
    public void handlePlayerLeftQueue(PlayerLeftQueueEvent event) {
        log.info("RABBITMQ: Received PlayerLeftQueue event - Player: {}", event.getPlayerId());

        try {
            matchmakingService.removePlayerFromQueue(event.getPlayerId());
        } catch (Exception e) {
            log.error("Error processing PlayerLeftQueue event", e);
        }
    }
    */
}