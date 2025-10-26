package com.gaming.player_service.infrastructure.adapter.output.persistence;

import com.gaming.player_service.domain.model.PlayerStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataPlayerStatsRepository extends MongoRepository<PlayerStats, String> {
    Optional<PlayerStats> findByPlayerId(String playerId);
    List<PlayerStats> findTop10ByOrderByRatingDesc();
}