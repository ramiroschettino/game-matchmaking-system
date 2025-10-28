package com.gaming.match_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Builder.Default
    private List<String> playerIds = new ArrayList<>();

    private Integer averageRating;

    public void addPlayer(String playerId) {
        this.playerIds.add(playerId);
    }

    public int getSize() {
        return playerIds.size();
    }

    public boolean isFull(int maxSize) {
        return playerIds.size() >= maxSize;
    }
}