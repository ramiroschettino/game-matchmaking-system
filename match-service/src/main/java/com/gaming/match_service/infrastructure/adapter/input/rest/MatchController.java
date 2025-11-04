package com.gaming.match_service.infrastructure.adapter.input.rest;

import com.gaming.match_service.domain.model.Match;
import com.gaming.match_service.domain.model.MatchStatus;
import com.gaming.match_service.domain.port.input.MatchCommands;
import com.gaming.match_service.domain.port.input.MatchQueries;
import com.gaming.match_service.domain.service.MatchmakingService;
import com.gaming.match_service.infrastructure.adapter.input.rest.dto.CompleteMatchRequest;
import com.gaming.match_service.infrastructure.adapter.input.rest.dto.MatchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchCommands matchCommands;
    private final MatchQueries matchQueries;
    private final MatchmakingService matchmakingService;

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponse> getMatch(@PathVariable String matchId) {
        log.info("REST: Getting match: {}", matchId);

        Match match = matchQueries.getMatchById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));

        return ResponseEntity.ok(MatchResponse.fromDomain(match));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<MatchResponse>> getRecentMatches(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("REST: Getting recent matches with limit: {}", limit);

        List<Match> matches = matchQueries.getRecentMatches(limit);
        List<MatchResponse> response = matches.stream()
                .map(MatchResponse::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{matchId}/complete")
    public ResponseEntity<MatchResponse> completeMatch(
            @PathVariable String matchId,
            @Valid @RequestBody CompleteMatchRequest request) {
        log.info("REST: Completing match: {} - Winner: {}", matchId, request.getWinnerTeam());

        Match match = matchCommands.completeMatch(
                matchId,
                request.getWinnerTeam(),
                request.getDurationSeconds()
        );

        return ResponseEntity.ok(MatchResponse.fromDomain(match));
    }

    @GetMapping("/queue/size")
    public ResponseEntity<Map<String, Integer>> getQueueSize() {
        int size = matchmakingService.getQueueSize();

        Map<String, Integer> response = new HashMap<>();
        response.put("queueSize", size);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{matchId}/start")
    public ResponseEntity<MatchResponse> startMatch(@PathVariable String matchId) {
        log.info("REST: Starting match: {}", matchId);

        Match match = matchCommands.startMatch(matchId);

        return ResponseEntity.ok(MatchResponse.fromDomain(match));
    }
}