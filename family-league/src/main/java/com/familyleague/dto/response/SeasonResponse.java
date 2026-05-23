package com.familyleague.dto.response;

import com.familyleague.enums.SeasonStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class SeasonResponse {
    private UUID id;
    private UUID leagueId;
    private String leagueName;
    private Integer seasonNumber;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private OffsetDateTime firstMatchStartsAt;
    private OffsetDateTime leaguePredictionLockAt;
    private SeasonStatus status;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
