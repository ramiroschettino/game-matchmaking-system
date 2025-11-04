package com.gaming.match_service.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteMatchRequest {

    @NotBlank(message = "Winner team is required (TEAM_A or TEAM_B)")
    private String winnerTeam;

    @Min(value = 1, message = "Duration must be at least 1 second")
    private Integer durationSeconds;
}