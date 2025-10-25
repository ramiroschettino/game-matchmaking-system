package com.gaming.player_service.domain.port.output;

import com.gaming.player_service.domain.model.Player;

import java.util.Optional;

public interface PlayerRepository {
    Player save(Player player);
    Optional<Player> findById(String id);
    Optional<Player> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}