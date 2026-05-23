package com.familyleague.service;

import com.familyleague.dto.request.CreateTeamRequest;
import com.familyleague.dto.request.UpdateTeamRequest;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.TeamResponse;

import java.util.UUID;

public interface TeamService {

    TeamResponse createTeam(CreateTeamRequest request);

    TeamResponse getTeamById(UUID id);

    PagedResponse<TeamResponse> getAllTeams(int page, int size, String sortBy, String sortDir, String search);

    TeamResponse updateTeam(UUID id, UpdateTeamRequest request);

    void deleteTeam(UUID id);
}
