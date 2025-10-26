package com.gaming.player_service.infrastructure.adapter.input.rest.dto;

import com.gaming.player_service.domain.model.Player;
import com.gaming.player_service.domain.model.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponse {
    private String id;
    private String username;
    private String email;
    private Integer rating;
    private PlayerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PlayerResponse fromDomain(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .username(player.getUsername())
                .email(player.getEmail())
                .rating(player.getRating())
                .status(player.getStatus())
                .createdAt(player.getCreatedAt())
                .updatedAt(player.getUpdatedAt())
                .build();
    }
}