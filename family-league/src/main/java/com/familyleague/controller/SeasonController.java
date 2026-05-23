package com.familyleague.controller;

import com.familyleague.dto.request.CreateSeasonRequest;
import com.familyleague.dto.request.UpdateSeasonRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.SeasonResponse;
import com.familyleague.dto.response.TeamResponse;
import com.familyleague.service.SeasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seasons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Seasons", description = "Season management APIs")
public class SeasonController {

    private final SeasonService seasonService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a season (Admin only)")
    public ResponseEntity<ApiResponse<SeasonResponse>> createSeason(
            @Valid @RequestBody CreateSeasonRequest request) {
        log.debug("POST /api/v1/seasons");
        SeasonResponse response = seasonService.createSeason(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Season created successfully", response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all seasons, optionally filtered by leagueId")
    public ResponseEntity<ApiResponse<PagedResponse<SeasonResponse>>> getAllSeasons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "seasonNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) UUID leagueId) {
        return ResponseEntity.ok(ApiResponse.ok(
                seasonService.getAllSeasons(page, size, sortBy, sortDir, leagueId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get season by ID")
    public ResponseEntity<ApiResponse<SeasonResponse>> getSeasonById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(seasonService.getSeasonById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a season (Admin only)")
    public ResponseEntity<ApiResponse<SeasonResponse>> updateSeason(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSeasonRequest request) {
        log.debug("PUT /api/v1/seasons/{}", id);
        return ResponseEntity.ok(ApiResponse.ok("Season updated successfully",
                seasonService.updateSeason(id, request)));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate a season (Admin only)")
    public ResponseEntity<ApiResponse<Void>> activateSeason(@PathVariable UUID id) {
        seasonService.activateSeason(id);
        return ResponseEntity.ok(ApiResponse.ok("Season activated", null));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark season as complete (Admin only)")
    public ResponseEntity<ApiResponse<Void>> completeSeason(@PathVariable UUID id) {
        seasonService.completeSeason(id);
        return ResponseEntity.ok(ApiResponse.ok("Season completed", null));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Close a season (Admin only)")
    public ResponseEntity<ApiResponse<Void>> closeSeason(@PathVariable UUID id) {
        seasonService.closeSeason(id);
        return ResponseEntity.ok(ApiResponse.ok("Season closed", null));
    }

    @PostMapping("/{seasonId}/teams")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a team to a season (Admin only)")
    public ResponseEntity<ApiResponse<Void>> addTeamToSeason(
            @PathVariable UUID seasonId,
            @RequestBody Map<String, UUID> body) {
        UUID teamId = body.get("teamId");
        seasonService.addTeamToSeason(seasonId, teamId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Team added to season", null));
    }

    @GetMapping("/{seasonId}/teams")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all teams in a season")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeamsForSeason(@PathVariable UUID seasonId) {
        return ResponseEntity.ok(ApiResponse.ok(seasonService.getTeamsForSeason(seasonId)));
    }

    @DeleteMapping("/{seasonId}/teams/{teamId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove a team from a season (Admin only)")
    public ResponseEntity<ApiResponse<Void>> removeTeamFromSeason(
            @PathVariable UUID seasonId,
            @PathVariable UUID teamId) {
        seasonService.removeTeamFromSeason(seasonId, teamId);
        return ResponseEntity.ok(ApiResponse.ok("Team removed from season", null));
    }
}
