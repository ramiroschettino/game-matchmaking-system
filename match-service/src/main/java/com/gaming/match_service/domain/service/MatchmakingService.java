package com.gaming.match_service.domain.service;

import com.gaming.match_service.domain.model.*;
import com.gaming.match_service.domain.port.output.EventPublisher;
import com.gaming.match_service.domain.port.output.EventStore;
import com.gaming.match_service.domain.port.output.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final EventStore eventStore;
    private final MatchRepository matchRepository;
    private final EventPublisher eventPublisher;

    // Cola de jugadores esperando (playerId -> rating)
    private final Map<String, PlayerInQueue> playersInQueue = new ConcurrentHashMap<>();

    private static final int TEAM_SIZE = 5;
    private static final int RATING_THRESHOLD = 100;  // Diferencia máxima de rating permitida

    public void addPlayerToQueue(String playerId, String username, int rating) {
        log.info("Adding player {} to matchmaking queue with rating {}", playerId, rating);

        PlayerInQueue player = new PlayerInQueue(playerId, username, rating, LocalDateTime.now());
        playersInQueue.put(playerId, player);

        // Intentar crear match
        tryCreateMatch();
    }

    public void removePlayerFromQueue(String playerId) {
        log.info("Removing player {} from matchmaking queue", playerId);
        playersInQueue.remove(playerId);
    }

    private void tryCreateMatch() {
        if (playersInQueue.size() < TEAM_SIZE * 2) {
            log.debug("Not enough players in queue: {}/10", playersInQueue.size());
            return;
        }

        log.info("Attempting to create match with {} players in queue", playersInQueue.size());

        // Obtener jugadores ordenados por rating
        List<PlayerInQueue> players = new ArrayList<>(playersInQueue.values());
        players.sort(Comparator.comparingInt(PlayerInQueue::getRating));

        // Intentar formar equipos balanceados
        List<PlayerInQueue> selectedPlayers = selectBalancedPlayers(players);

        if (selectedPlayers.size() == TEAM_SIZE * 2) {
            createMatchForPlayers(selectedPlayers);
        } else {
            log.debug("Could not find balanced teams");
        }
    }

    private List<PlayerInQueue> selectBalancedPlayers(List<PlayerInQueue> players) {
        // Algoritmo simple: tomar los primeros 10 jugadores con ratings similares
        List<PlayerInQueue> selected = new ArrayList<>();

        for (int i = 0; i <= players.size() - TEAM_SIZE * 2; i++) {
            List<PlayerInQueue> candidates = players.subList(i, i + TEAM_SIZE * 2);

            int minRating = candidates.get(0).getRating();
            int maxRating = candidates.get(candidates.size() - 1).getRating();

            if (maxRating - minRating <= RATING_THRESHOLD) {
                selected = new ArrayList<>(candidates);
                break;
            }
        }

        return selected;
    }

    private void createMatchForPlayers(List<PlayerInQueue> players) {
        String matchId = UUID.randomUUID().toString();

        log.info("Creating match {} for {} players", matchId, players.size());

        // Dividir en dos equipos (alternando para balancear)
        List<String> teamAIds = new ArrayList<>();
        List<String> teamBIds = new ArrayList<>();
        int sumRatingA = 0;
        int sumRatingB = 0;

        for (int i = 0; i < players.size(); i++) {
            PlayerInQueue player = players.get(i);
            if (i % 2 == 0) {
                teamAIds.add(player.getPlayerId());
                sumRatingA += player.getRating();
            } else {
                teamBIds.add(player.getPlayerId());
                sumRatingB += player.getRating();
            }
        }

        int avgRatingA = sumRatingA / TEAM_SIZE;
        int avgRatingB = sumRatingB / TEAM_SIZE;

        // Crear evento
        MatchCreatedEvent event = MatchCreatedEvent.builder()
                .matchId(matchId)
                .eventType("MatchCreated")
                .teamAPlayerIds(teamAIds)
                .teamBPlayerIds(teamBIds)
                .averageRatingTeamA(avgRatingA)
                .averageRatingTeamB(avgRatingB)
                .timestamp(LocalDateTime.now())
                .version(1)
                .build();

        // Guardar evento
        eventStore.saveEvent(event);

        // Crear proyección
        Match match = Match.fromEvents(List.of(event));
        matchRepository.save(match);

        // Remover jugadores de la cola
        players.forEach(p -> playersInQueue.remove(p.getPlayerId()));

        // Publicar evento
        List<String> allPlayerIds = match.getAllPlayerIds();
        eventPublisher.publishMatchFound(matchId, allPlayerIds);

        log.info("Match {} created successfully with teams: A={}, B={}",
                matchId, teamAIds.size(), teamBIds.size());
    }

    public int getQueueSize() {
        return playersInQueue.size();
    }

    // Inner class para representar jugador en cola
    private static class PlayerInQueue {
        private final String playerId;
        private final String username;
        private final int rating;
        private final LocalDateTime joinedAt;

        public PlayerInQueue(String playerId, String username, int rating, LocalDateTime joinedAt) {
            this.playerId = playerId;
            this.username = username;
            this.rating = rating;
            this.joinedAt = joinedAt;
        }

        public String getPlayerId() { return playerId; }
        public String getUsername() { return username; }
        public int getRating() { return rating; }
        public LocalDateTime getJoinedAt() { return joinedAt; }
    }
}