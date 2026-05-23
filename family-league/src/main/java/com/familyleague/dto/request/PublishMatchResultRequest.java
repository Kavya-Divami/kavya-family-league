package com.familyleague.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PublishMatchResultRequest {

    private UUID winnerTeamId;

    @NotNull(message = "Toss winner team ID is required")
    private UUID tossWinnerTeamId;

    @NotNull(message = "Player of match ID is required")
    private UUID playerOfMatchId;

    private boolean isTie = false;
}
