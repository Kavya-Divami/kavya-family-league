package com.familyleague.service;

import com.familyleague.dto.response.LeaderboardEntryResponse;
import com.familyleague.dto.response.PagedResponse;

import java.util.UUID;

public interface LeaderboardService {

    PagedResponse<LeaderboardEntryResponse> getLeaderboard(UUID seasonId, int page, int size);
}
