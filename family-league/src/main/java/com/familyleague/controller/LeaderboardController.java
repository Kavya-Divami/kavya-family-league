package com.familyleague.controller;

import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.LeaderboardEntryResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seasons/{seasonId}/leaderboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leaderboard", description = "Season leaderboard APIs")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get season leaderboard ordered by rank")
    public ResponseEntity<ApiResponse<PagedResponse<LeaderboardEntryResponse>>> getLeaderboard(
            @PathVariable UUID seasonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("GET /api/v1/seasons/{}/leaderboard", seasonId);
        return ResponseEntity.ok(ApiResponse.ok(leaderboardService.getLeaderboard(seasonId, page, size)));
    }
}
