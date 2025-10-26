package com.gaming.player_service.domain.service;

import com.gaming.player_service.domain.model.Player;
import com.gaming.player_service.domain.model.PlayerStats;
import com.gaming.player_service.domain.model.PlayerStatus;
import com.gaming.player_service.domain.port.input.PlayerCommands;
import com.gaming.player_service.domain.port.input.PlayerQueries;
import com.gaming.player_service.domain.port.output.PlayerRepository;
import com.gaming.player_service.domain.port.output.PlayerStatsRepository;
import com.gaming.player_service.domain.port.output.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService implements PlayerCommands, PlayerQueries {

    private final PlayerRepository playerRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final EventPublisher eventPublisher;

    // ==================== COMANDOS ====================

    @Override
    public Player createPlayer(String username, String email) {
        log.info("Creando player with username: {}", username);

        // Validaciones
        if (playerRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        if (playerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // Crear player (Write Model)
        Player player = Player.builder()
                .username(username)
                .email(email)
                .rating(1000)
                .status(PlayerStatus.IDLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Player savedPlayer = playerRepository.save(player);

        // Crear stats iniciales (Read Model)
        PlayerStats stats = PlayerStats.builder()
                .playerId(savedPlayer.getId())
                .username(savedPlayer.getUsername())
                .rating(savedPlayer.getRating())
                .matchesPlayed(0)
                .wins(0)
                .losses(0)
                .winRate(0.0)
                .currentStreak(0)
                .build();

        playerStatsRepository.save(stats);

        log.info("Player created successfully: {}", savedPlayer.getId());
        return savedPlayer;
    }

    @Override
    public Player joinQueue(String playerId) {
        log.info("Player {} joining queue", playerId);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        // Domain logic
        player.joinQueue();

        Player updatedPlayer = playerRepository.save(player);

        // Publish event
        eventPublisher.publishPlayerJoinedQueue(
                player.getId(),
                player.getUsername(),
                player.getRating()
        );

        log.info("Player {} joined queue successfully", playerId);
        return updatedPlayer;
    }

    @Override
    public Player leaveQueue(String playerId) {
        log.info("Player {} leaving queue", playerId);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        if (player.getStatus() != PlayerStatus.IN_QUEUE) {
            throw new IllegalStateException("Player is not in queue");
        }

        player.setStatus(PlayerStatus.IDLE);
        player.setUpdatedAt(LocalDateTime.now());

        Player updatedPlayer = playerRepository.save(player);

        eventPublisher.publishPlayerLeftQueue(playerId);

        log.info("Player {} left queue successfully", playerId);
        return updatedPlayer;
    }

    @Override
    public Player updateRating(String playerId, int newRating) {
        log.info("Updating rating for player {}: {}", playerId, newRating);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        int oldRating = player.getRating();

        // Domain logic
        player.updateRating(newRating);

        Player updatedPlayer = playerRepository.save(player);

        // Update read model (stats)
        PlayerStats stats = playerStatsRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Stats not found: " + playerId));

        stats.setRating(newRating);
        playerStatsRepository.save(stats);

        // Publish event
        eventPublisher.publishPlayerRatingUpdated(playerId, oldRating, newRating);

        log.info("Player {} rating updated from {} to {}", playerId, oldRating, newRating);
        return updatedPlayer;
    }

    @Override
    public void updatePlayerStatus(String playerId, String status) {
        log.info("Updating status for player {}: {}", playerId, status);

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        PlayerStatus newStatus = PlayerStatus.valueOf(status.toUpperCase());
        player.setStatus(newStatus);
        player.setUpdatedAt(LocalDateTime.now());

        playerRepository.save(player);

        log.info("Player {} status updated to {}", playerId, status);
    }

    // ==================== QUERYS ====================

    @Override
    public Optional<Player> getPlayerById(String playerId) {
        log.debug("Fetching player by id: {}", playerId);
        return playerRepository.findById(playerId);
    }

    @Override
    public Optional<Player> getPlayerByUsername(String username) {
        log.debug("Fetching player by username: {}", username);
        return playerRepository.findByUsername(username);
    }

    @Override
    public PlayerStats getPlayerStats(String playerId) {
        log.debug("Fetching stats for player: {}", playerId);
        return playerStatsRepository.findByPlayerId(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Stats not found for player: " + playerId));
    }

    @Override
    public List<PlayerStats> getLeaderboard(int limit) {
        log.debug("Fetching leaderboard with limit: {}", limit);
        return playerStatsRepository.findTopByRating(limit);
    }
}