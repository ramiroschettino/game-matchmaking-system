package com.gaming.player_service.domain.port.output;

import com.gaming.player_service.domain.model.PlayerStats;

import java.util.List;
import java.util.Optional;

public interface PlayerStatsRepository {
    PlayerStats save(PlayerStats stats);
    Optional<PlayerStats> findByPlayerId(String playerId);
    List<PlayerStats> findTopByRating(int limit);
    void updateStats(String playerId, boolean won, int newRating);
}