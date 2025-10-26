package com.gaming.player_service.infrastructure.adapter.output.persistence;

import com.gaming.player_service.domain.model.Player;
import com.gaming.player_service.domain.port.output.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlayerRepositoryAdapter implements PlayerRepository {

    private final SpringDataPlayerRepository repository;

    @Override
    public Player save(Player player) {
        return repository.save(player);
    }

    @Override
    public Optional<Player> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Player> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}