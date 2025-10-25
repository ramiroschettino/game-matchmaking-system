package com.gaming.player_service.domain.port.input;

import com.gaming.player_service.domain.model.Player;

public interface PlayerCommands {
    Player createPlayer(String username, String email);
    Player joinQueue(String playerId);
    Player leaveQueue(String playerId);
    Player updateRating(String playerId, int newRating);
    void updatePlayerStatus(String playerId, String status);
}