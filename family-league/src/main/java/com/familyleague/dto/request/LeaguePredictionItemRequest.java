package com.familyleague.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class LeaguePredictionItemRequest {

    @NotNull(message = "Position is required")
    @Min(value = 1, message = "Position must be at least 1")
    private Integer position;

    @NotNull(message = "Team ID is required")
    private UUID teamId;
}
