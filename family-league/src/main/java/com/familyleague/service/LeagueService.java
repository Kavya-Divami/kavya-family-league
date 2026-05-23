package com.familyleague.service;

import com.familyleague.dto.request.CreateLeagueRequest;
import com.familyleague.dto.request.UpdateLeagueRequest;
import com.familyleague.dto.response.LeagueResponse;
import com.familyleague.dto.response.PagedResponse;

import java.util.UUID;

public interface LeagueService {

    LeagueResponse createLeague(CreateLeagueRequest request);

    LeagueResponse getLeagueById(UUID id);

    PagedResponse<LeagueResponse> getAllLeagues(int page, int size, String sortBy, String sortDir, String search);

    LeagueResponse updateLeague(UUID id, UpdateLeagueRequest request);

    void closeLeague(UUID id);

    void deleteLeague(UUID id);
}
