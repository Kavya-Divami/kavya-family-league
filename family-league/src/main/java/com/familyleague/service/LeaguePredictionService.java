package com.familyleague.service;

import com.familyleague.dto.request.SubmitLeaguePredictionRequest;
import com.familyleague.dto.response.LeaguePredictionResponse;

import java.util.List;
import java.util.UUID;

public interface LeaguePredictionService {

    LeaguePredictionResponse submitPrediction(UUID seasonId, SubmitLeaguePredictionRequest request, String username);

    LeaguePredictionResponse getMyPrediction(UUID seasonId, String username);

    List<LeaguePredictionResponse> getPredictionsForSeason(UUID seasonId, String requestingUsername);
}
