package com.familyleague.service;

import com.familyleague.dto.request.CreatePlayerRequest;
import com.familyleague.dto.request.UpdatePlayerRequest;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.PlayerResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PlayerService {

    PlayerResponse createPlayer(UUID teamId, CreatePlayerRequest request);

    PlayerResponse getPlayerById(UUID id);

    PagedResponse<PlayerResponse> getPlayersByTeam(UUID teamId, Pageable pageable);

    PlayerResponse updatePlayer(UUID id, UpdatePlayerRequest request);

    void deletePlayer(UUID id);
}
