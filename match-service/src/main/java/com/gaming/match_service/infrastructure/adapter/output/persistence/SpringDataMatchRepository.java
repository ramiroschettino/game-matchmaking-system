package com.gaming.match_service.infrastructure.adapter.output.persistence;

import com.gaming.match_service.domain.model.Match;
import com.gaming.match_service.domain.model.MatchStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMatchRepository extends MongoRepository<Match, String> {
    List<Match> findByStatus(MatchStatus status);

    @Query("{ $or: [ { 'teamA.playerIds': ?0 }, { 'teamB.playerIds': ?0 } ] }")
    List<Match> findByPlayerId(String playerId);

    List<Match> findTop10ByOrderByCreatedAtDesc();
}