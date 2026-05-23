package com.familyleague.service;

import com.familyleague.dto.request.CreateMatchRequest;
import com.familyleague.dto.request.UpdateMatchRequest;
import com.familyleague.dto.response.MatchResponse;
import com.familyleague.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MatchService {

    MatchResponse createMatch(CreateMatchRequest request);

    MatchResponse getMatchById(UUID id);

    PagedResponse<MatchResponse> getMatchesBySeason(UUID seasonId, Pageable pageable);

    PagedResponse<MatchResponse> getAllMatches(int page, int size, String sortBy, String sortDir, UUID seasonId);

    MatchResponse updateMatch(UUID id, UpdateMatchRequest request);

    void deleteMatch(UUID id);
}
