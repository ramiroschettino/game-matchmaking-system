package com.gaming.player_service.infrastructure.adapter.input.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchCompletedEvent implements Serializable {
    private String matchId;
    private String winnerTeam;
    private List<String> teamAPlayers;
    private List<String> teamBPlayers;
}