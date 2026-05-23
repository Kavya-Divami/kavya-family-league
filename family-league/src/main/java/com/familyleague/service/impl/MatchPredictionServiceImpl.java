package com.familyleague.service.impl;

import com.familyleague.dto.request.SubmitMatchPredictionRequest;
import com.familyleague.dto.response.MatchPredictionResponse;
import com.familyleague.entity.*;
import com.familyleague.exception.PredictionLockedException;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.*;
import com.familyleague.service.MatchPredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MatchPredictionServiceImpl implements MatchPredictionService {

    private final MatchPredictionRepository matchPredictionRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MatchPredictionResponse submitPrediction(UUID matchId, SubmitMatchPredictionRequest request, String username) {
        log.debug("Submitting prediction for matchId={} by user={}", matchId, username);

        Match match = matchRepository.findByIdAndIsDeletedFalse(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match", matchId));

        // Check lock
        if (OffsetDateTime.now().isAfter(match.getLockAt())) {
            throw new PredictionLockedException("Predictions are locked for this match");
        }

        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Team predictedWinner = teamRepository.findByIdAndIsDeletedFalse(request.getPredictedWinnerTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team", request.getPredictedWinnerTeamId()));

        Team predictedTossWinner = teamRepository.findByIdAndIsDeletedFalse(request.getPredictedTossWinnerTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team", request.getPredictedTossWinnerTeamId()));

        Player predictedPotm = playerRepository.findByIdAndIsDeletedFalse(request.getPredictedPotmPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player", request.getPredictedPotmPlayerId()));

        MatchPrediction prediction = matchPredictionRepository
                .findByUserIdAndMatchId(user.getId(), matchId)
                .orElse(MatchPrediction.builder().user(user).match(match).build());

        prediction.setPredictedWinnerTeam(predictedWinner);
        prediction.setPredictedTossWinnerTeam(predictedTossWinner);
        prediction.setPredictedPotmPlayer(predictedPotm);
        prediction.setSubmittedAt(OffsetDateTime.now());
        prediction.setIsLocked(false);

        MatchPrediction saved = matchPredictionRepository.save(prediction);
        log.info("Prediction submitted/updated for matchId={} userId={}", matchId, user.getId());
        return toResponse(saved);
    }

    @Override
    public MatchPredictionResponse getMyPrediction(UUID matchId, String username) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        MatchPrediction prediction = matchPredictionRepository
                .findByUserIdAndMatchId(user.getId(), matchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No prediction found for this match"));
        return toResponse(prediction);
    }

    @Override
    public List<MatchPredictionResponse> getPredictionsForMatch(UUID matchId, String requestingUsername) {
        Match match = matchRepository.findByIdAndIsDeletedFalse(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match", matchId));

        User requestingUser = userRepository.findByUsernameAndIsDeletedFalse(requestingUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + requestingUsername));

        // Only return all predictions if match is locked (lockAt is past)
        boolean isLocked = OffsetDateTime.now().isAfter(match.getLockAt());
        if (isLocked) {
            return matchPredictionRepository.findAllByMatchIdAndIsLockedTrue(matchId)
                    .stream().map(this::toResponse).toList();
        } else {
            // Return only the requesting user's prediction
            return matchPredictionRepository
                    .findByUserIdAndMatchId(requestingUser.getId(), matchId)
                    .map(p -> List.of(toResponse(p)))
                    .orElse(List.of());
        }
    }

    private MatchPredictionResponse toResponse(MatchPrediction p) {
        return MatchPredictionResponse.builder()
                .id(p.getId())
                .matchId(p.getMatch().getId())
                .userId(p.getUser().getId())
                .username(p.getUser().getUsername())
                .predictedWinnerTeamId(p.getPredictedWinnerTeam() != null ? p.getPredictedWinnerTeam().getId() : null)
                .predictedWinnerTeamName(p.getPredictedWinnerTeam() != null ? p.getPredictedWinnerTeam().getName() : null)
                .predictedTossWinnerTeamId(p.getPredictedTossWinnerTeam() != null ? p.getPredictedTossWinnerTeam().getId() : null)
                .predictedTossWinnerTeamName(p.getPredictedTossWinnerTeam() != null ? p.getPredictedTossWinnerTeam().getName() : null)
                .predictedPotmPlayerId(p.getPredictedPotmPlayer() != null ? p.getPredictedPotmPlayer().getId() : null)
                .predictedPotmPlayerName(p.getPredictedPotmPlayer() != null ? p.getPredictedPotmPlayer().getName() : null)
                .submittedAt(p.getSubmittedAt())
                .isLocked(p.getIsLocked())
                .pointsAwarded(p.getPointsAwarded())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
