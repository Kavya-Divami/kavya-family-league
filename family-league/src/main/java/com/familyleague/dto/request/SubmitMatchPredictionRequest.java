package com.familyleague.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SubmitMatchPredictionRequest {

    @NotNull(message = "Predicted winner team ID is required")
    private UUID predictedWinnerTeamId;

    @NotNull(message = "Predicted toss winner team ID is required")
    private UUID predictedTossWinnerTeamId;

    @NotNull(message = "Predicted player of match ID is required")
    private UUID predictedPotmPlayerId;
}
