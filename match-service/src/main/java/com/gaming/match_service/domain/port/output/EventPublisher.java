package com.gaming.match_service.domain.port.output;

import java.util.List;

public interface EventPublisher {
    void publishMatchFound(String matchId, List<String> playerIds);
    void publishMatchStarted(String matchId);
    void publishMatchCompleted(String matchId, String winnerTeam, List<String> teamAPlayers, List<String> teamBPlayers);
}