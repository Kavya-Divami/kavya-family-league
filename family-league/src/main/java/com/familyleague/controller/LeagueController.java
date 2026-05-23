package com.familyleague.controller;

import com.familyleague.dto.request.CreateLeagueRequest;
import com.familyleague.dto.request.UpdateLeagueRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.LeagueResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.service.LeagueService;
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
@RequestMapping("/api/v1/leagues")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Leagues", description = "League management APIs")
public class LeagueController {

    private final LeagueService leagueService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new league (Admin only)")
    public ResponseEntity<ApiResponse<LeagueResponse>> createLeague(
            @Valid @RequestBody CreateLeagueRequest request) {
        log.debug("POST /api/v1/leagues");
        LeagueResponse response = leagueService.createLeague(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("League created successfully", response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all leagues with optional search and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<LeagueResponse>>> getAllLeagues(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(
                leagueService.getAllLeagues(page, size, sortBy, sortDir, search)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get league by ID")
    public ResponseEntity<ApiResponse<LeagueResponse>> getLeagueById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(leagueService.getLeagueById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a league (Admin only)")
    public ResponseEntity<ApiResponse<LeagueResponse>> updateLeague(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLeagueRequest request) {
        log.debug("PUT /api/v1/leagues/{}", id);
        return ResponseEntity.ok(ApiResponse.ok("League updated successfully",
                leagueService.updateLeague(id, request)));
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Close a league (Admin only)")
    public ResponseEntity<ApiResponse<Void>> closeLeague(@PathVariable UUID id) {
        leagueService.closeLeague(id);
        return ResponseEntity.ok(ApiResponse.ok("League closed", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a league (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteLeague(@PathVariable UUID id) {
        log.debug("DELETE /api/v1/leagues/{}", id);
        leagueService.deleteLeague(id);
        return ResponseEntity.ok(ApiResponse.ok("League deleted", null));
    }
}
