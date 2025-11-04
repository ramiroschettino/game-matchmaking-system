package com.gaming.player_service.infrastructure.adapter.input.messaging;

import com.gaming.player_service.domain.port.input.PlayerCommands;
import com.gaming.player_service.domain.port.output.PlayerStatsRepository;
import com.gaming.player_service.infrastructure.adapter.input.messaging.dto.MatchCompletedEvent;
import com.gaming.player_service.infrastructure.adapter.input.messaging.dto.MatchFoundEvent;
import com.gaming.player_service.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchEventListener {

    private final PlayerCommands playerCommands;
    private final PlayerStatsRepository playerStatsRepository;

    @RabbitListener(queues = RabbitMQConfig.MATCH_FOUND_QUEUE)
    public void handleMatchFound(MatchFoundEvent event) {
        log.info("RABBITMQ: Received MatchFound event - Match: {}, Players: {}",
                event.getMatchId(), event.getPlayerIds().size());

        try {
            // Actualizar todos los jugadores a IN_MATCH
            for (String playerId : event.getPlayerIds()) {
                playerCommands.updatePlayerStatus(playerId, "IN_MATCH");
            }

            log.info("All players updated to IN_MATCH status");
        } catch (Exception e) {
            log.error("Error processing MatchFound event", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.MATCH_COMPLETED_QUEUE)
    public void handleMatchCompleted(MatchCompletedEvent event) {
        log.info("RABBITMQ: Received MatchCompleted event - Match: {}, Winner: {}",
                event.getMatchId(), event.getWinnerTeam());

        try {

            boolean teamAWon = "TEAM_A".equals(event.getWinnerTeam());

            for (String playerId : event.getTeamAPlayers()) {
                int ratingChange = teamAWon ? 25 : -20;
                updatePlayerAfterMatch(playerId, ratingChange, teamAWon);
            }

            for (String playerId : event.getTeamBPlayers()) {
                int ratingChange = teamAWon ? -20 : 25;
                updatePlayerAfterMatch(playerId, ratingChange, !teamAWon);
            }

            log.info("All players updated after match completion");
        } catch (Exception e) {
            log.error("Error processing MatchCompleted event", e);
        }
    }

    private void updatePlayerAfterMatch(String playerId, int ratingChange, boolean won) {
        try {

            var playerOpt = playerCommands.getPlayerById(playerId);

            if (playerOpt.isEmpty()) {
                log.error("Player not found: {}", playerId);
                return;
            }

            var player = playerOpt.get();


            int newRating = Math.max(0, player.getRating() + ratingChange);


            playerCommands.updateRating(playerId, newRating);

            playerStatsRepository.updateStats(playerId, won, newRating);

            playerCommands.updatePlayerStatus(playerId, "IDLE");

            log.info("Player {} updated: rating {} -> {}, wins/losses updated, status -> IDLE",
                    playerId, player.getRating(), newRating);
        } catch (Exception e) {
            log.error("Error updating player {} after match", playerId, e);
        }
    }
}