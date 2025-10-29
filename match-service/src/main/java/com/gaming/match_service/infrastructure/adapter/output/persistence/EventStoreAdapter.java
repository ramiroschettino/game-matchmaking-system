package com.gaming.match_service.infrastructure.adapter.output.persistence;

import com.gaming.match_service.domain.model.MatchEvent;
import com.gaming.match_service.domain.port.output.EventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component  // ← IMPORTANTE que tenga @Component
@RequiredArgsConstructor
public class EventStoreAdapter implements EventStore {

    private final SpringDataEventRepository repository;

    @Override
    public void saveEvent(MatchEvent event) {
        log.debug("Saving event: {} for match: {}", event.getEventType(), event.getMatchId());
        repository.save(event);
    }

    @Override
    public List<MatchEvent> getEventsByMatchId(String matchId) {
        log.debug("Getting events for match: {}", matchId);
        return repository.findByMatchIdOrderByVersionAsc(matchId);
    }

    @Override
    public List<MatchEvent> getAllEvents() {
        log.debug("Getting all events");
        return repository.findAll();
    }
}