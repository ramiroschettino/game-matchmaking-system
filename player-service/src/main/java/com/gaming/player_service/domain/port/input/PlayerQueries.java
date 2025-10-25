package com.gaming.player_service.domain.port.input;

import com.gaming.player_service.domain.model.Player;
import com.gaming.player_service.domain.model.PlayerStats;

import java.util.List;
import java.util.Optional;

public interface PlayerQueries {
    Optional<Player> getPlayerById(String playerId);
    Optional<Player> getPlayerByUsername(String username);
    PlayerStats getPlayerStats(String playerId);
    List<PlayerStats> getLeaderboard(int limit);
}