package com.gaming.match_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MatchCreatedEvent extends MatchEvent {

    private List<String> teamAPlayerIds;
    private List<String> teamBPlayerIds;
    private Integer averageRatingTeamA;
    private Integer averageRatingTeamB;

    @Override
    public void apply(Match match) {
        match.setId(getMatchId());
        match.setStatus(MatchStatus.WAITING_FOR_PLAYERS);

        Team teamA = Team.builder()
                .playerIds(teamAPlayerIds)
                .averageRating(averageRatingTeamA)
                .build();

        Team teamB = Team.builder()
                .playerIds(teamBPlayerIds)
                .averageRating(averageRatingTeamB)
                .build();

        match.setTeamA(teamA);
        match.setTeamB(teamB);
        match.setCreatedAt(getTimestamp());
    }
}