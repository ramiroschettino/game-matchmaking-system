package com.gaming.match_service.domain.port.input;

import com.gaming.match_service.domain.model.Match;
import com.gaming.match_service.domain.model.MatchStatus;

import java.util.List;
import java.util.Optional;

public interface MatchQueries {
    Optional<Match> getMatchById(String matchId);
    List<Match> getMatchesByStatus(MatchStatus status);
    List<Match> getMatchesByPlayerId(String playerId);
    List<Match> getRecentMatches(int limit);
}