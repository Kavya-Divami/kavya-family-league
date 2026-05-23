package com.familyleague.dto.response;

import com.familyleague.enums.LeagueStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class LeagueResponse {
    private UUID id;
    private String name;
    private String description;
    private LeagueStatus status;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
