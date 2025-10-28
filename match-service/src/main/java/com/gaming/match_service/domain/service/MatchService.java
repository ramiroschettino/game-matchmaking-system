package com.gaming.match_service.domain.service;

import com.gaming.match_service.domain.model.*;
import com.gaming.match_service.domain.port.input.MatchCommands;
import com.gaming.match_service.domain.port.input.MatchQueries;
import com.gaming.match_service.domain.port.output.EventPublisher;
import com.gaming.match_service.domain.port.output.EventStore;
import com.gaming.match_service.domain.port.output.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService implements MatchCommands, MatchQueries {

    private final EventStore eventStore;
    private final MatchRepository matchRepository;
    private final EventPublisher eventPublisher;

    // ==================== COMMANDS (WRITE - Event Sourcing) ====================

    @Override
    public Match createMatch(String matchId) {
        log.info("Creating match: {}", matchId);

        // Por ahora creamos un match vacío, el matchmaking lo va a llenar
        MatchCreatedEvent event = MatchCreatedEvent.builder()
                .matchId(matchId)
                .eventType("MatchCreated")
                .timestamp(LocalDateTime.now())
                .version(1)
                .build();

        // Guardar evento
        eventStore.saveEvent(event);

        // Reconstruir Match desde eventos
        List<MatchEvent> events = eventStore.getEventsByMatchId(matchId);
        Match match = Match.fromEvents(events);

        // Guardar proyección (para queries rápidas)
        matchRepository.save(match);

        log.info("Match created: {}", matchId);
        return match;
    }

    @Override
    public Match startMatch(String matchId) {
        log.info("Starting match: {}", matchId);

        // Reconstruir Match desde eventos
        List<MatchEvent> events = eventStore.getEventsByMatchId(matchId);
        Match match = Match.fromEvents(events);

        if (match.getStatus() != MatchStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Match must be WAITING_FOR_PLAYERS to start");
        }

        // Crear evento
        MatchStartedEvent event = MatchStartedEvent.builder()
                .matchId(matchId)
                .eventType("MatchStarted")
                .timestamp(LocalDateTime.now())
                .version(match.getVersion() + 1)
                .build();

        // Guardar evento
        eventStore.saveEvent(event);

        // Aplicar evento al match
        event.apply(match);
        match.setVersion(event.getVersion());

        // Actualizar proyección
        matchRepository.save(match);

        // Publicar evento
        eventPublisher.publishMatchStarted(matchId);

        log.info("Match started: {}", matchId);
        return match;
    }

    @Override
    public Match completeMatch(String matchId, String winnerTeam, int durationSeconds) {
        log.info("Completing match: {} - Winner: {}", matchId, winnerTeam);

        // Reconstruir Match desde eventos
        List<MatchEvent> events = eventStore.getEventsByMatchId(matchId);
        Match match = Match.fromEvents(events);

        if (match.getStatus() != MatchStatus.IN_PROGRESS) {
            throw new IllegalStateException("Match must be IN_PROGRESS to complete");
        }

        // Crear evento
        MatchCompletedEvent event = MatchCompletedEvent.builder()
                .matchId(matchId)
                .eventType("MatchCompleted")
                .winnerTeam(winnerTeam)
                .durationSeconds(durationSeconds)
                .timestamp(LocalDateTime.now())
                .version(match.getVersion() + 1)
                .build();

        // Guardar evento
        eventStore.saveEvent(event);

        // Aplicar evento
        event.apply(match);
        match.setVersion(event.getVersion());

        // Actualizar proyección
        matchRepository.save(match);

        // Publicar evento
        List<String> teamAPlayers = match.getTeamA().getPlayerIds();
        List<String> teamBPlayers = match.getTeamB().getPlayerIds();
        eventPublisher.publishMatchCompleted(matchId, winnerTeam, teamAPlayers, teamBPlayers);

        log.info("Match completed: {}", matchId);
        return match;
    }

    @Override
    public void cancelMatch(String matchId) {
        log.info("Cancelling match: {}", matchId);

        // Reconstruir Match
        List<MatchEvent> events = eventStore.getEventsByMatchId(matchId);
        Match match = Match.fromEvents(events);

        match.setStatus(MatchStatus.CANCELLED);
        matchRepository.save(match);

        log.info("Match cancelled: {}", matchId);
    }

    // ==================== QUERIES (READ) ====================

    @Override
    public Optional<Match> getMatchById(String matchId) {
        log.debug("Getting match by id: {}", matchId);
        return matchRepository.findById(matchId);
    }

    @Override
    public List<Match> getMatchesByStatus(MatchStatus status) {
        log.debug("Getting matches by status: {}", status);
        return matchRepository.findByStatus(status);
    }

    @Override
    public List<Match> getMatchesByPlayerId(String playerId) {
        log.debug("Getting matches by player id: {}", playerId);
        return matchRepository.findByPlayerId(playerId);
    }

    @Override
    public List<Match> getRecentMatches(int limit) {
        log.debug("Getting recent matches with limit: {}", limit);
        return matchRepository.findRecentMatches(limit);
    }
}