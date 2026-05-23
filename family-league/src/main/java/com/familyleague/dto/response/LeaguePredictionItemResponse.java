package com.familyleague.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LeaguePredictionItemResponse {
    private UUID id;
    private Integer position;
    private UUID teamId;
    private String teamName;
}
