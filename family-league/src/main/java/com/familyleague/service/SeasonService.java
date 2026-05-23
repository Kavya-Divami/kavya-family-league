package com.familyleague.service;

import com.familyleague.dto.request.CreateSeasonRequest;
import com.familyleague.dto.request.UpdateSeasonRequest;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.SeasonResponse;
import com.familyleague.dto.response.TeamResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SeasonService {

    SeasonResponse createSeason(CreateSeasonRequest request);

    SeasonResponse getSeasonById(UUID id);

    PagedResponse<SeasonResponse> getSeasonsByLeague(UUID leagueId, Pageable pageable);

    PagedResponse<SeasonResponse> getAllSeasons(int page, int size, String sortBy, String sortDir, UUID leagueId);

    SeasonResponse updateSeason(UUID id, UpdateSeasonRequest request);

    void activateSeason(UUID id);

    void completeSeason(UUID id);

    void closeSeason(UUID id);

    void addTeamToSeason(UUID seasonId, UUID teamId);

    List<TeamResponse> getTeamsForSeason(UUID seasonId);

    void removeTeamFromSeason(UUID seasonId, UUID teamId);
}
