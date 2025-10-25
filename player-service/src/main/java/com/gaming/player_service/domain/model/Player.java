package com.gaming.player_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "players")
public class Player {

    @Id
    private String id;

    private String username;

    private String email;

    @Builder.Default
    private Integer rating = 1000; // Rating inicial (ELO)

    @Builder.Default
    private PlayerStatus status = PlayerStatus.IDLE;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Domain methods
    public void joinQueue() {
        if (this.status != PlayerStatus.IDLE) {
            throw new IllegalStateException("Player must be IDLE to join queue");
        }
        this.status = PlayerStatus.IN_QUEUE;
        this.updatedAt = LocalDateTime.now();
    }

    public void startMatch() {
        if (this.status != PlayerStatus.IN_QUEUE) {
            throw new IllegalStateException("Player must be IN_QUEUE to start match");
        }
        this.status = PlayerStatus.IN_MATCH;
        this.updatedAt = LocalDateTime.now();
    }

    public void finishMatch() {
        if (this.status != PlayerStatus.IN_MATCH) {
            throw new IllegalStateException("Player must be IN_MATCH to finish");
        }
        this.status = PlayerStatus.IDLE;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateRating(int newRating) {
        if (newRating < 0) {
            throw new IllegalArgumentException("Rating cannot be negative");
        }
        this.rating = newRating;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canJoinQueue() {
        return this.status == PlayerStatus.IDLE;
    }
}
