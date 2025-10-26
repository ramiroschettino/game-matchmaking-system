package com.gaming.player_service.infrastructure.adapter.output.persistence;

import com.gaming.player_service.domain.model.PlayerStats;
import com.gaming.player_service.domain.port.output.PlayerStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlayerStatsRepositoryAdapter implements PlayerStatsRepository {

    private final SpringDataPlayerStatsRepository repository;

    @Override
    public PlayerStats save(PlayerStats stats) {
        return repository.save(stats);
    }

    @Override
    public Optional<PlayerStats> findByPlayerId(String playerId) {
        return repository.findByPlayerId(playerId);
    }

    @Override
    public List<PlayerStats> findTopByRating(int limit) {
        return repository.findTop10ByOrderByRatingDesc();
    }

    @Override
    public void updateStats(String playerId, boolean won, int newRating) {
        Optional<PlayerStats> statsOpt = repository.findByPlayerId(playerId);

        if (statsOpt.isPresent()) {
            PlayerStats stats = statsOpt.get();

            if (won) {
                stats.addWin();
            } else {
                stats.addLoss();
            }

            stats.setRating(newRating);
            repository.save(stats);
        }
    }
}