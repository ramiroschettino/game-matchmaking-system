package com.gaming.match_service.infrastructure.adapter.output.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchFoundEvent implements Serializable {
    private String matchId;
    private List<String> playerIds;
}