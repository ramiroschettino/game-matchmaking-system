package com.gaming.match_service.infrastructure.adapter.output.persistence;

import com.gaming.match_service.domain.model.Match;
import com.gaming.match_service.domain.model.MatchStatus;
import com.gaming.match_service.domain.port.output.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchRepositoryAdapter implements MatchRepository {

    private final SpringDataMatchRepository repository;

    @Override
    public Match save(Match match) {
        log.debug("Saving match: {}", match.getId());
        return repository.save(match);
    }

    @Override
    public Optional<Match> findById(String matchId) {
        log.debug("Finding match by id: {}", matchId);
        return repository.findById(matchId);
    }

    @Override
    public List<Match> findByStatus(MatchStatus status) {
        log.debug("Finding matches by status: {}", status);
        return repository.findByStatus(status);
    }

    @Override
    public List<Match> findByPlayerId(String playerId) {
        log.debug("Finding matches by player id: {}", playerId);
        return repository.findByPlayerId(playerId);
    }

    @Override
    public List<Match> findRecentMatches(int limit) {
        log.debug("Finding recent matches with limit: {}", limit);
        return repository.findTop10ByOrderByCreatedAtDesc();
    }
}