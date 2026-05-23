package com.familyleague.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CreateMatchRequest {

    @NotNull(message = "Season ID is required")
    private UUID seasonId;

    @NotNull(message = "Home team ID is required")
    private UUID homeTeamId;

    @NotNull(message = "Away team ID is required")
    private UUID awayTeamId;

    @NotNull(message = "Scheduled date/time is required")
    private OffsetDateTime scheduledAt;

    private Integer matchNumber;

    private String venue;
}
