package com.familyleague.dto.response;

import com.familyleague.enums.MatchStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MatchResponse {
    private UUID id;
    private UUID seasonId;
    private Integer seasonNumber;
    private UUID homeTeamId;
    private String homeTeamName;
    private UUID awayTeamId;
    private String awayTeamName;
    private Integer matchNumber;
    private String venue;
    private OffsetDateTime scheduledAt;
    private OffsetDateTime lockAt;
    private MatchStatus status;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
