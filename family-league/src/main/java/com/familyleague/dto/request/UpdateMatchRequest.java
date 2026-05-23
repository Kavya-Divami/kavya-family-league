package com.familyleague.dto.request;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UpdateMatchRequest {

    private UUID homeTeamId;

    private UUID awayTeamId;

    private OffsetDateTime scheduledAt;

    private Integer matchNumber;

    private String venue;
}
