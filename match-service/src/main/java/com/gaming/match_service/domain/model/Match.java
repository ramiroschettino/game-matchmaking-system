package com.gaming.match_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "matches")
public class Match {

    @Id
    private String id;

    private Team teamA;

    private Team teamB;

    private MatchStatus status;

    private String winnerTeam;  // "TEAM_A", "TEAM_B", o null

    private Integer durationSeconds;

    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Builder.Default
    private Integer version = 0;  // Para event sourcing

    public static Match fromEvents(List<MatchEvent> events) {
        Match match = new Match();

        for (MatchEvent event : events) {
            event.apply(match);
            match.version = event.getVersion();
        }

        return match;
    }

    public List<String> getAllPlayerIds() {
        List<String> allPlayers = new ArrayList<>();
        if (teamA != null) {
            allPlayers.addAll(teamA.getPlayerIds());
        }
        if (teamB != null) {
            allPlayers.addAll(teamB.getPlayerIds());
        }
        return allPlayers;
    }
}