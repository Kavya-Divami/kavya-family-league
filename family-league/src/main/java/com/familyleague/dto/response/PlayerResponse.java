package com.familyleague.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PlayerResponse {
    private UUID id;
    private UUID teamId;
    private String teamName;
    private String name;
    private Integer jerseyNumber;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
