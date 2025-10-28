package com.gaming.match_service.domain.port.output;

import com.gaming.match_service.domain.model.MatchEvent;

import java.util.List;

public interface EventStore {
    void saveEvent(MatchEvent event);
    List<MatchEvent> getEventsByMatchId(String matchId);
    List<MatchEvent> getAllEvents();
}