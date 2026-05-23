package com.familyleague.controller;

import com.familyleague.dto.request.PublishMatchResultRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.MatchResultResponse;
import com.familyleague.service.MatchResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matches/{matchId}/result")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Match Results", description = "Match result APIs")
public class MatchResultController {

    private final MatchResultService matchResultService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Publish match result (Admin only)")
    public ResponseEntity<ApiResponse<MatchResultResponse>> publishResult(
            @PathVariable UUID matchId,
            @Valid @RequestBody PublishMatchResultRequest request,
            Authentication auth) {
        log.debug("POST /api/v1/matches/{}/result", matchId);
        MatchResultResponse response = matchResultService.publishResult(matchId, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Match result published", response));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update match result (Admin only)")
    public ResponseEntity<ApiResponse<MatchResultResponse>> updateResult(
            @PathVariable UUID matchId,
            @Valid @RequestBody PublishMatchResultRequest request,
            Authentication auth) {
        log.debug("PUT /api/v1/matches/{}/result", matchId);
        MatchResultResponse response = matchResultService.publishResult(matchId, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Match result updated", response));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get match result")
    public ResponseEntity<ApiResponse<MatchResultResponse>> getResult(@PathVariable UUID matchId) {
        return ResponseEntity.ok(ApiResponse.ok(matchResultService.getResultByMatch(matchId)));
    }
}
