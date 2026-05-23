package com.familyleague.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MatchPredictionResponse {
    private UUID id;
    private UUID matchId;
    private UUID userId;
    private String username;
    private UUID predictedWinnerTeamId;
    private String predictedWinnerTeamName;
    private UUID predictedTossWinnerTeamId;
    private String predictedTossWinnerTeamName;
    private UUID predictedPotmPlayerId;
    private String predictedPotmPlayerName;
    private OffsetDateTime submittedAt;
    private Boolean isLocked;
    private Integer pointsAwarded;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
