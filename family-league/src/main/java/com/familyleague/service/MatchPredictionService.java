package com.familyleague.service;

import com.familyleague.dto.request.SubmitMatchPredictionRequest;
import com.familyleague.dto.response.MatchPredictionResponse;

import java.util.List;
import java.util.UUID;

public interface MatchPredictionService {

    MatchPredictionResponse submitPrediction(UUID matchId, SubmitMatchPredictionRequest request, String username);

    MatchPredictionResponse getMyPrediction(UUID matchId, String username);

    List<MatchPredictionResponse> getPredictionsForMatch(UUID matchId, String requestingUsername);
}
