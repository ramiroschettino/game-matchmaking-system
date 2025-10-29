package com.gaming.match_service.infrastructure.adapter.output.persistence;

import com.gaming.match_service.domain.model.MatchEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataEventRepository extends MongoRepository<MatchEvent, String> {
    List<MatchEvent> findByMatchIdOrderByVersionAsc(String matchId);
}