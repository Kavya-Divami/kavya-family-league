package com.familyleague.controller;

import com.familyleague.dto.request.SubmitMatchPredictionRequest;
import com.familyleague.dto.response.ApiResponse;
import com.familyleague.dto.response.MatchPredictionResponse;
import com.familyleague.service.MatchPredictionService;
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
@RequestMapping("/api/v1/matches/{matchId}/predictions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Match Predictions", description = "Match prediction APIs")
public class MatchPredictionController {

    private final MatchPredictionService matchPredictionService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Submit or update a match prediction")
    public ResponseEntity<ApiResponse<MatchPredictionResponse>> submitPrediction(
            @PathVariable UUID matchId,
            @Valid @RequestBody SubmitMatchPredictionRequest request,
            Authentication auth) {
        log.debug("POST /api/v1/matches/{}/predictions by {}", matchId, auth.getName());
        MatchPredictionResponse response = matchPredictionService.submitPrediction(matchId, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Prediction submitted", response));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get my prediction for a match")
    public ResponseEntity<ApiResponse<MatchPredictionResponse>> getMyPrediction(
            @PathVariable UUID matchId,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                matchPredictionService.getMyPrediction(matchId, auth.getName())));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all predictions for a match (only visible after lock)")
    public ResponseEntity<ApiResponse<List<MatchPredictionResponse>>> getPredictionsForMatch(
            @PathVariable UUID matchId,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok(
                matchPredictionService.getPredictionsForMatch(matchId, auth.getName())));
    }
}
