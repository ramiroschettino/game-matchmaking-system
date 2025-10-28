package com.gaming.match_service.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MatchStartedEvent extends MatchEvent {

    @Override
    public void apply(Match match) {
        match.setStatus(MatchStatus.IN_PROGRESS);
        match.setStartedAt(getTimestamp());
    }
}