package com.familyleague.controller;

import com.familyleague.dto.request.CreateMatchRequest;
import com.familyleague.dto.request.UpdateMatchRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.MatchResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.service.MatchService;
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
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Matches", description = "Match management APIs")
public class MatchController {

    private final MatchService matchService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a match (Admin only)")
    public ResponseEntity<ApiResponse<MatchResponse>> createMatch(
            @Valid @RequestBody CreateMatchRequest request) {
        log.debug("POST /api/v1/matches");
        MatchResponse response = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Match created successfully", response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all matches, optionally filtered by seasonId")
    public ResponseEntity<ApiResponse<PagedResponse<MatchResponse>>> getAllMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "scheduledAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) UUID seasonId) {
        return ResponseEntity.ok(ApiResponse.ok(
                matchService.getAllMatches(page, size, sortBy, sortDir, seasonId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get match by ID")
    public ResponseEntity<ApiResponse<MatchResponse>> getMatchById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(matchService.getMatchById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a match (Admin only)")
    public ResponseEntity<ApiResponse<MatchResponse>> updateMatch(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMatchRequest request) {
        log.debug("PUT /api/v1/matches/{}", id);
        return ResponseEntity.ok(ApiResponse.ok("Match updated successfully",
                matchService.updateMatch(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a match (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteMatch(@PathVariable UUID id) {
        log.debug("DELETE /api/v1/matches/{}", id);
        matchService.deleteMatch(id);
        return ResponseEntity.ok(ApiResponse.ok("Match deleted", null));
    }
}
