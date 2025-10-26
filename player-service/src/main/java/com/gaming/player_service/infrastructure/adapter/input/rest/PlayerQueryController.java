package com.gaming.player_service.infrastructure.adapter.input.rest;

import com.gaming.player_service.domain.model.Player;
import com.gaming.player_service.domain.model.PlayerStats;
import com.gaming.player_service.domain.port.input.PlayerQueries;
import com.gaming.player_service.infrastructure.adapter.input.rest.dto.PlayerResponse;
import com.gaming.player_service.infrastructure.adapter.input.rest.dto.PlayerStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerQueryController {

    private final PlayerQueries playerQueries;

    @GetMapping("/{playerId}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable String playerId) {
        log.info("REST: Getting player by id: {}", playerId);

        Player player = playerQueries.getPlayerById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));

        return ResponseEntity.ok(PlayerResponse.fromDomain(player));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<PlayerResponse> getPlayerByUsername(@PathVariable String username) {
        log.info("REST: Getting player by username: {}", username);

        Player player = playerQueries.getPlayerByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + username));

        return ResponseEntity.ok(PlayerResponse.fromDomain(player));
    }

    @GetMapping("/{playerId}/stats")
    public ResponseEntity<PlayerStatsResponse> getPlayerStats(@PathVariable String playerId) {
        log.info("REST: Getting stats for player: {}", playerId);

        PlayerStats stats = playerQueries.getPlayerStats(playerId);

        return ResponseEntity.ok(PlayerStatsResponse.fromDomain(stats));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<PlayerStatsResponse>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("REST: Getting leaderboard with limit: {}", limit);

        List<PlayerStats> leaderboard = playerQueries.getLeaderboard(limit);

        List<PlayerStatsResponse> response = leaderboard.stream()
                .map(PlayerStatsResponse::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}