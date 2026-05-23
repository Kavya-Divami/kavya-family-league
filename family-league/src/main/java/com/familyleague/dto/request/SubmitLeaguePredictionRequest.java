package com.familyleague.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SubmitLeaguePredictionRequest {

    @NotEmpty(message = "Prediction items are required")
    private List<LeaguePredictionItemRequest> items;
}
