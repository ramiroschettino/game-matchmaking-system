package com.gaming.player_service.infrastructure.adapter.output.persistence;

import com.gaming.player_service.domain.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataPlayerRepository extends MongoRepository<Player, String> {
    Optional<Player> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}