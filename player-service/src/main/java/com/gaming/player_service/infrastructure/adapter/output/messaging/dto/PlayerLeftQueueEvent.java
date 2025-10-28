package com.gaming.player_service.infrastructure.adapter.output.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerLeftQueueEvent implements Serializable {
    private String playerId;
}