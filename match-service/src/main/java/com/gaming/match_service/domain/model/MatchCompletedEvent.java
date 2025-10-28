package com.gaming.match_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MatchCompletedEvent extends MatchEvent {

    private String winnerTeam;  
    private Integer durationSeconds;

    @Override
    public void apply(Match match) {
        match.setStatus(MatchStatus.COMPLETED);
        match.setWinnerTeam(winnerTeam);
        match.setDurationSeconds(durationSeconds);
        match.setCompletedAt(getTimestamp());
    }
}