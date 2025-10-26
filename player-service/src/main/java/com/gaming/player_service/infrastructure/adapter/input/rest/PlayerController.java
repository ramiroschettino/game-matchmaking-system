package com.gaming.player_service.infrastructure.adapter.input.rest;

import com.gaming.player_service.domain.model.Player;
import com.gaming.player_service.domain.port.input.PlayerCommands;
import com.gaming.player_service.infrastructure.adapter.input.rest.dto.CreatePlayerRequest;
import com.gaming.player_service.infrastructure.adapter.input.rest.dto.PlayerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerCommands playerCommands;

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        log.info("REST: Creating player with username: {}", request.getUsername());

        Player player = playerCommands.createPlayer(request.getUsername(), request.getEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PlayerResponse.fromDomain(player));
    }

    @PostMapping("/{playerId}/queue/join")
    public ResponseEntity<PlayerResponse> joinQueue(@PathVariable String playerId) {
        log.info("REST: Player {} joining queue", playerId);

        Player player = playerCommands.joinQueue(playerId);

        return ResponseEntity.ok(PlayerResponse.fromDomain(player));
    }

    @PostMapping("/{playerId}/queue/leave")
    public ResponseEntity<PlayerResponse> leaveQueue(@PathVariable String playerId) {
        log.info("REST: Player {} leaving queue", playerId);

        Player player = playerCommands.leaveQueue(playerId);

        return ResponseEntity.ok(PlayerResponse.fromDomain(player));
    }

    @PutMapping("/{playerId}/rating")
    public ResponseEntity<PlayerResponse> updateRating(
            @PathVariable String playerId,
            @RequestParam Integer rating) {
        log.info("REST: Updating rating for player {}: {}", playerId, rating);

        Player player = playerCommands.updateRating(playerId, rating);

        return ResponseEntity.ok(PlayerResponse.fromDomain(player));
    }
}