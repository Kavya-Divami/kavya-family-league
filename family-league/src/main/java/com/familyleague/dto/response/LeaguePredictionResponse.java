package com.familyleague.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LeaguePredictionResponse {
    private UUID id;
    private UUID seasonId;
    private UUID userId;
    private String username;
    private OffsetDateTime submittedAt;
    private Boolean isLocked;
    private List<LeaguePredictionItemResponse> items;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
