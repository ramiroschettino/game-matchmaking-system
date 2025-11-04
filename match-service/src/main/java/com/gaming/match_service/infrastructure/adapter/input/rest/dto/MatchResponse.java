package com.gaming.match_service.infrastructure.adapter.input.rest.dto;

import com.gaming.match_service.domain.model.Match;
import com.gaming.match_service.domain.model.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private String id;
    private List<String> teamAPlayerIds;
    private List<String> teamBPlayerIds;
    private Integer averageRatingTeamA;
    private Integer averageRatingTeamB;
    private MatchStatus status;
    private String winnerTeam;
    private Integer durationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public static MatchResponse fromDomain(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .teamAPlayerIds(match.getTeamA() != null ? match.getTeamA().getPlayerIds() : null)
                .teamBPlayerIds(match.getTeamB() != null ? match.getTeamB().getPlayerIds() : null)
                .averageRatingTeamA(match.getTeamA() != null ? match.getTeamA().getAverageRating() : null)
                .averageRatingTeamB(match.getTeamB() != null ? match.getTeamB().getAverageRating() : null)
                .status(match.getStatus())
                .winnerTeam(match.getWinnerTeam())
                .durationSeconds(match.getDurationSeconds())
                .createdAt(match.getCreatedAt())
                .startedAt(match.getStartedAt())
                .completedAt(match.getCompletedAt())
                .build();
    }
}