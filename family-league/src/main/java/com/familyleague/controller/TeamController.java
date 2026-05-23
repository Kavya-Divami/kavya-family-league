package com.familyleague.controller;

import com.familyleague.dto.request.CreateTeamRequest;
import com.familyleague.dto.request.UpdateTeamRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.TeamResponse;
import com.familyleague.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teams", description = "Team management APIs")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new team (Admin only)")
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(
            @Valid @RequestBody CreateTeamRequest request) {
        log.debug("POST /api/v1/teams");
        TeamResponse response = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Team created successfully", response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all teams with optional search and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<TeamResponse>>> getAllTeams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(
                teamService.getAllTeams(page, size, sortBy, sortDir, search)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(teamService.getTeamById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a team (Admin only)")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeamRequest request) {
        log.debug("PUT /api/v1/teams/{}", id);
        return ResponseEntity.ok(ApiResponse.ok("Team updated successfully",
                teamService.updateTeam(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a team (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable UUID id) {
        log.debug("DELETE /api/v1/teams/{}", id);
        teamService.deleteTeam(id);
        return ResponseEntity.ok(ApiResponse.ok("Team deleted", null));
    }
}
