package com.gaming.match_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "match_events")
public abstract class MatchEvent {

    @Id
    private String id;

    private String matchId;

    private String eventType;

    private LocalDateTime timestamp;

    private Integer version;

    public abstract void apply(Match match);
}