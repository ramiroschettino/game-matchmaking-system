package com.gaming.player_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "player_stats")
public class PlayerStats {

    @Id
    private String playerId;

    private String username;

    private Integer rating;

    @Builder.Default
    private Integer matchesPlayed = 0;

    @Builder.Default
    private Integer wins = 0;

    @Builder.Default
    private Integer losses = 0;

    @Builder.Default
    private Double winRate = 0.0;

    @Builder.Default
    private Integer currentStreak = 0;

    private Integer rank;

    public void calculateWinRate() {
        if (matchesPlayed > 0) {
            this.winRate = (double) wins / matchesPlayed;
        } else {
            this.winRate = 0.0;
        }
    }

    public void addWin() {
        this.wins++;
        this.matchesPlayed++;
        if (currentStreak >= 0) {
            this.currentStreak++;
        } else {
            this.currentStreak = 1;
        }
        calculateWinRate();
    }

    public void addLoss() {
        this.losses++;
        this.matchesPlayed++;
        if (currentStreak <= 0) {
            this.currentStreak--;
        } else {
            this.currentStreak = -1;
        }
        calculateWinRate();
    }
}