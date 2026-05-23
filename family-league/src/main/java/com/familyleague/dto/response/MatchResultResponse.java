package com.familyleague.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MatchResultResponse {
    private UUID id;
    private UUID matchId;
    private UUID winnerTeamId;
    private String winnerTeamName;
    private UUID tossWinnerTeamId;
    private String tossWinnerTeamName;
    private UUID playerOfMatchId;
    private String playerOfMatchName;
    private Boolean isTie;
    private OffsetDateTime publishedAt;
    private String publishedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
