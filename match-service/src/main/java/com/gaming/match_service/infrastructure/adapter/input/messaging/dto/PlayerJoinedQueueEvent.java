package com.gaming.match_service.infrastructure.adapter.input.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJoinedQueueEvent implements Serializable {
    private String playerId;
    private String username;
    private Integer rating;
}