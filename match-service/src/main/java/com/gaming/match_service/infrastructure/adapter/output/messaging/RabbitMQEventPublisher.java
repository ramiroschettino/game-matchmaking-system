package com.gaming.match_service.infrastructure.adapter.output.messaging;

import com.gaming.match_service.domain.port.output.EventPublisher;
import com.gaming.match_service.infrastructure.adapter.output.messaging.dto.MatchCompletedEventDto;
import com.gaming.match_service.infrastructure.adapter.output.messaging.dto.MatchFoundEvent;
import com.gaming.match_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishMatchFound(String matchId, List<String> playerIds) {
        log.info("Publishing MatchFound event: Match {} with {} players", matchId, playerIds.size());

        MatchFoundEvent event = MatchFoundEvent.builder()
                .matchId(matchId)
                .playerIds(playerIds)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.GAME_EVENTS_EXCHANGE,
                RabbitMQConfig.MATCH_FOUND_KEY,
                event
        );

        log.info("MatchFound event published successfully");
    }

    @Override
    public void publishMatchStarted(String matchId) {
        log.info("Publishing MatchStarted event: Match {}", matchId);
        // Por ahora solo log, podés agregar lógica después
    }

    @Override
    public void publishMatchCompleted(String matchId, String winnerTeam,
                                      List<String> teamAPlayers, List<String> teamBPlayers) {
        log.info("Publishing MatchCompleted event: Match {} - Winner: {}", matchId, winnerTeam);

        MatchCompletedEventDto event = MatchCompletedEventDto.builder()
                .matchId(matchId)
                .winnerTeam(winnerTeam)
                .teamAPlayers(teamAPlayers)
                .teamBPlayers(teamBPlayers)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.GAME_EVENTS_EXCHANGE,
                RabbitMQConfig.MATCH_COMPLETED_KEY,
                event
        );

        log.info("MatchCompleted event published successfully");
    }
}