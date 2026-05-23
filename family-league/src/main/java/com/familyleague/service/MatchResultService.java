package com.familyleague.service;

import com.familyleague.dto.request.PublishMatchResultRequest;
import com.familyleague.dto.response.MatchResultResponse;

import java.util.UUID;

public interface MatchResultService {

    MatchResultResponse publishResult(UUID matchId, PublishMatchResultRequest request, String publishedBy);

    MatchResultResponse getResultByMatch(UUID matchId);
}
