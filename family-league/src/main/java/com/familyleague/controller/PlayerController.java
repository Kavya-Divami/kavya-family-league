package com.familyleague.controller;

import com.familyleague.dto.request.CreatePlayerRequest;
import com.familyleague.dto.request.UpdatePlayerRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.PlayerResponse;
import com.familyleague.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Players", description = "Player management APIs")
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping("/api/v1/teams/{teamId}/players")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a player to a team (Admin only)")
    public ResponseEntity<ApiResponse<PlayerResponse>> createPlayer(
            @PathVariable UUID teamId,
            @Valid @RequestBody CreatePlayerRequest request) {
        log.debug("POST /api/v1/teams/{}/players", teamId);
        PlayerResponse response = playerService.createPlayer(teamId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Player created successfully", response));
    }

    @GetMapping("/api/v1/teams/{teamId}/players")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get players by team")
    public ResponseEntity<ApiResponse<PagedResponse<PlayerResponse>>> getPlayersByTeam(
            @PathVariable UUID teamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(ApiResponse.ok(
                playerService.getPlayersByTeam(teamId, PageRequest.of(page, size, sort))));
    }

    @GetMapping("/api/v1/players/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get player by ID")
    public ResponseEntity<ApiResponse<PlayerResponse>> getPlayerById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(playerService.getPlayerById(id)));
    }

    @PutMapping("/api/v1/players/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a player (Admin only)")
    public ResponseEntity<ApiResponse<PlayerResponse>> updatePlayer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlayerRequest request) {
        log.debug("PUT /api/v1/players/{}", id);
        return ResponseEntity.ok(ApiResponse.ok("Player updated successfully",
                playerService.updatePlayer(id, request)));
    }

    @DeleteMapping("/api/v1/players/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a player (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deletePlayer(@PathVariable UUID id) {
        log.debug("DELETE /api/v1/players/{}", id);
        playerService.deletePlayer(id);
        return ResponseEntity.ok(ApiResponse.ok("Player deleted", null));
    }
}
