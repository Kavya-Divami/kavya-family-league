package com.familyleague.service.impl;

import com.familyleague.dto.request.LeaguePredictionItemRequest;
import com.familyleague.dto.request.SubmitLeaguePredictionRequest;
import com.familyleague.dto.response.LeaguePredictionItemResponse;
import com.familyleague.dto.response.LeaguePredictionResponse;
import com.familyleague.entity.*;
import com.familyleague.exception.PredictionLockedException;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.*;
import com.familyleague.service.LeaguePredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeaguePredictionServiceImpl implements LeaguePredictionService {

    private final LeaguePredictionRepository leaguePredictionRepository;
    private final SeasonRepository seasonRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LeaguePredictionResponse submitPrediction(UUID seasonId, SubmitLeaguePredictionRequest request, String username) {
        log.debug("Submitting league prediction for seasonId={} by user={}", seasonId, username);

        Season season = seasonRepository.findByIdAndIsDeletedFalse(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season", seasonId));

        if (season.getLeaguePredictionLockAt() != null
                && OffsetDateTime.now().isAfter(season.getLeaguePredictionLockAt())) {
            throw new PredictionLockedException("Predictions are locked for this season");
        }

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        LeaguePrediction prediction = leaguePredictionRepository
                .findByUserIdAndSeasonId(user.getId(), seasonId)
                .orElse(LeaguePrediction.builder().user(user).season(season).build());

        // Replace items
        prediction.getItems().clear();
        prediction.setSubmittedAt(OffsetDateTime.now());

        LeaguePrediction saved = leaguePredictionRepository.save(prediction);

        List<LeaguePredictionItem> items = new ArrayList<>();
        for (LeaguePredictionItemRequest itemReq : request.getItems()) {
            Team team = teamRepository.findByIdAndIsDeletedFalse(itemReq.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team", itemReq.getTeamId()));
            LeaguePredictionItem item = LeaguePredictionItem.builder()
                    .leaguePrediction(saved)
                    .position(itemReq.getPosition())
                    .team(team)
                    .build();
            items.add(item);
        }
        saved.getItems().addAll(items);
        LeaguePrediction finalSaved = leaguePredictionRepository.save(saved);

        log.info("League prediction submitted for seasonId={} userId={}", seasonId, user.getId());
        return toResponse(finalSaved);
    }

    @Override
    public LeaguePredictionResponse getMyPrediction(UUID seasonId, String username) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        LeaguePrediction prediction = leaguePredictionRepository
                .findByUserIdAndSeasonId(user.getId(), seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("No league prediction found for this season"));
        return toResponse(prediction);
    }

    @Override
    public List<LeaguePredictionResponse> getPredictionsForSeason(UUID seasonId, String requestingUsername) {
        Season season = seasonRepository.findByIdAndIsDeletedFalse(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season", seasonId));

        User requestingUser = userRepository.findByUsernameAndIsDeletedFalse(requestingUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestingUsername));

        boolean isLocked = season.getLeaguePredictionLockAt() != null
                && OffsetDateTime.now().isAfter(season.getLeaguePredictionLockAt());

        if (isLocked) {
            return leaguePredictionRepository.findAllBySeasonId(seasonId, org.springframework.data.domain.Pageable.unpaged())
                    .getContent().stream().map(this::toResponse).toList();
        } else {
            return leaguePredictionRepository
                    .findByUserIdAndSeasonId(requestingUser.getId(), seasonId)
                    .map(p -> List.of(toResponse(p)))
                    .orElse(List.of());
        }
    }

    private LeaguePredictionResponse toResponse(LeaguePrediction p) {
        List<LeaguePredictionItemResponse> itemResponses = p.getItems().stream()
                .map(item -> LeaguePredictionItemResponse.builder()
                        .id(item.getId())
                        .position(item.getPosition())
                        .teamId(item.getTeam().getId())
                        .teamName(item.getTeam().getName())
                        .build())
                .toList();

        return LeaguePredictionResponse.builder()
                .id(p.getId())
                .seasonId(p.getSeason().getId())
                .userId(p.getUser().getId())
                .username(p.getUser().getUsername())
                .submittedAt(p.getSubmittedAt())
                .isLocked(p.getIsLocked())
                .items(itemResponses)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
