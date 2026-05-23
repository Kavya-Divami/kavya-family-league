package com.familyleague.controller;

import com.familyleague.dto.request.SubmitLeaguePredictionRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.LeaguePredictionResponse;
import com.familyleague.service.LeaguePredictionService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seasons/{seasonId}/predictions/league")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "League Predictions", description = "League prediction APIs")
public class LeaguePredictionController {

    private final LeaguePredictionService leaguePredictionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit or update a league prediction for the season")
    public ResponseEntity<ApiResponse<LeaguePredictionResponse>> submitPrediction(
            @PathVariable UUID seasonId,
            @Valid @RequestBody SubmitLeaguePredictionRequest request,
            Authentication auth) {
        log.debug("POST /api/v1/seasons/{}/predictions/league by {}", seasonId, auth.getName());
        LeaguePredictionResponse response = leaguePredictionService.submitPrediction(seasonId, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("League prediction submitted", response));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my league prediction for a season")
    public ResponseEntity<ApiResponse<LeaguePredictionResponse>> getMyPrediction(
            @PathVariable UUID seasonId,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                leaguePredictionService.getMyPrediction(seasonId, auth.getName())));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all league predictions for a season (only after lock)")
    public ResponseEntity<ApiResponse<List<LeaguePredictionResponse>>> getPredictionsForSeason(
            @PathVariable UUID seasonId,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                leaguePredictionService.getPredictionsForSeason(seasonId, auth.getName())));
    }
}
