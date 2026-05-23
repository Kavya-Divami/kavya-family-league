package com.familyleague.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CreateSeasonRequest {

    @NotNull(message = "League ID is required")
    private UUID leagueId;

    @NotNull(message = "Season number is required")
    private Integer seasonNumber;

    private OffsetDateTime startDate;

    private OffsetDateTime endDate;

    private OffsetDateTime firstMatchStartsAt;

    private OffsetDateTime leaguePredictionLockAt;
}
