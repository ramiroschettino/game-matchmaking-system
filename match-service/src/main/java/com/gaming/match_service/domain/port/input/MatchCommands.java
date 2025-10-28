package com.gaming.match_service.domain.port.input;

import com.gaming.match_service.domain.model.Match;

public interface MatchCommands {
    Match createMatch(String matchId);
    Match startMatch(String matchId);
    Match completeMatch(String matchId, String winnerTeam, int durationSeconds);
    void cancelMatch(String matchId);
}