package com.gaming.match_service.domain.port.output;

import com.gaming.match_service.domain.model.Match;
import com.gaming.match_service.domain.model.MatchStatus;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {
    Match save(Match match);
    Optional<Match> findById(String matchId);
    List<Match> findByStatus(MatchStatus status);
    List<Match> findByPlayerId(String playerId);
    List<Match> findRecentMatches(int limit);
}