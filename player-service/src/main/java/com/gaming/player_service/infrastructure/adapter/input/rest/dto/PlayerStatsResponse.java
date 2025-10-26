package com.gaming.player_service.infrastructure.adapter.input.rest.dto;

import com.gaming.player_service.domain.model.PlayerStats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatsResponse {
    private String playerId;
    private String username;
    private Integer rating;
    private Integer matchesPlayed;
    private Integer wins;
    private Integer losses;
    private Double winRate;
    private Integer currentStreak;
    private Integer rank;

    public static PlayerStatsResponse fromDomain(PlayerStats stats) {
        return PlayerStatsResponse.builder()
                .playerId(stats.getPlayerId())
                .username(stats.getUsername())
                .rating(stats.getRating())
                .matchesPlayed(stats.getMatchesPlayed())
                .wins(stats.getWins())
                .losses(stats.getLosses())
                .winRate(stats.getWinRate())
                .currentStreak(stats.getCurrentStreak())
                .rank(stats.getRank())
                .build();
    }
}