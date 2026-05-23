package com.familyleague.dto.request;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UpdateSeasonRequest {

    private Integer seasonNumber;

    private OffsetDateTime startDate;

    private OffsetDateTime endDate;

    private OffsetDateTime firstMatchStartsAt;

    private OffsetDateTime leaguePredictionLockAt;
}
