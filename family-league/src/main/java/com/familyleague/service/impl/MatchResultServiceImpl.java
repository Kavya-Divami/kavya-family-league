package com.familyleague.service.impl;

import com.familyleague.dto.request.PublishMatchResultRequest;
import com.familyleague.dto.response.MatchResultResponse;
import com.familyleague.entity.*;
import com.familyleague.enums.MatchStatus;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.*;
import com.familyleague.service.MatchResultService;
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
public class MatchResultServiceImpl implements MatchResultService {

    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchPredictionRepository matchPredictionRepository;
    private final SeasonLeaderboardRepository seasonLeaderboardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MatchResultResponse publishResult(UUID matchId, PublishMatchResultRequest request, String publishedBy) {
        log.debug("Publishing result for matchId={}", matchId);

        Match match = matchRepository.findByIdAndIsDeletedFalse(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match", matchId));

        Team tossWinnerTeam = teamRepository.findByIdAndIsDeletedFalse(request.getTossWinnerTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Toss winner team", request.getTossWinnerTeamId()));

        Player playerOfMatch = playerRepository.findByIdAndIsDeletedFalse(request.getPlayerOfMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Player of match", request.getPlayerOfMatchId()));

        Team winnerTeam = null;
        if (!request.isTie() && request.getWinnerTeamId() != null) {
            winnerTeam = teamRepository.findByIdAndIsDeletedFalse(request.getWinnerTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Winner team", request.getWinnerTeamId()));
        }

        // Set match status to COMPLETED
        match.setStatus(MatchStatus.COMPLETED);
        matchRepository.save(match);

        // Create or update MatchResult
        MatchResult matchResult = matchResultRepository.findByMatchId(matchId)
                .orElse(MatchResult.builder().match(match).build());

        matchResult.setWinnerTeam(winnerTeam);
        matchResult.setTossWinnerTeam(tossWinnerTeam);
        matchResult.setPlayerOfMatch(playerOfMatch);
        matchResult.setIsTie(request.isTie());
        matchResult.setPublishedAt(OffsetDateTime.now());
        matchResult.setPublishedBy(publishedBy);
        MatchResult savedResult = matchResultRepository.save(matchResult);

        // Recalculate points for all predictions for this match
        recalculatePoints(match, savedResult);

        log.info("Published result for matchId={}", matchId);
        return toResponse(savedResult);
    }

    @Override
    public MatchResultResponse getResultByMatch(UUID matchId) {
        matchRepository.findByIdAndIsDeletedFalse(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match", matchId));
        MatchResult result = matchResultRepository.findByMatchId(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match result not found for matchId: " + matchId));
        return toResponse(result);
    }

    private void recalculatePoints(Match match, MatchResult result) {
        List<MatchPrediction> predictions = matchPredictionRepository.findAllByMatchIdAndIsLockedTrue(match.getId());

        for (MatchPrediction prediction : predictions) {
            int points = 0;

            // Winner prediction: 1 point
            if (!result.getIsTie() && result.getWinnerTeam() != null
                    && prediction.getPredictedWinnerTeam() != null
                    && result.getWinnerTeam().getId().equals(prediction.getPredictedWinnerTeam().getId())) {
                points++;
            }
            // Toss winner prediction: 1 point
            if (result.getTossWinnerTeam() != null
                    && prediction.getPredictedTossWinnerTeam() != null
                    && result.getTossWinnerTeam().getId().equals(prediction.getPredictedTossWinnerTeam().getId())) {
                points++;
            }
            // Player of match prediction: 1 point
            if (result.getPlayerOfMatch() != null
                    && prediction.getPredictedPotmPlayer() != null
                    && result.getPlayerOfMatch().getId().equals(prediction.getPredictedPotmPlayer().getId())) {
                points++;
            }

            prediction.setPointsAwarded(points);
            matchPredictionRepository.save(prediction);

            // Update leaderboard
            upsertLeaderboard(prediction.getUser(), match.getSeason());
        }

        // Recalculate ranks for the season
        recalculateRanks(match.getSeason().getId());
    }

    private void upsertLeaderboard(User user, Season season) {
        // Sum all points for this user in this season
        List<MatchPrediction> allPredictions = matchPredictionRepository
                .findAllByUserIdAndMatch_SeasonId(user.getId(), season.getId());
        int totalPoints = allPredictions.stream()
                .mapToInt(p -> p.getPointsAwarded() != null ? p.getPointsAwarded() : 0)
                .sum();

        SeasonLeaderboard entry = seasonLeaderboardRepository
                .findByUserIdAndSeasonId(user.getId(), season.getId())
                .orElse(SeasonLeaderboard.builder().user(user).season(season).build());
        entry.setTotalPoints(totalPoints);
        seasonLeaderboardRepository.save(entry);
    }

    private void recalculateRanks(UUID seasonId) {
        List<SeasonLeaderboard> entries = seasonLeaderboardRepository
                .findAllBySeasonIdOrderByTotalPointsDesc(seasonId);
        int rank = 1;
        for (SeasonLeaderboard entry : entries) {
            entry.setRank(rank++);
            seasonLeaderboardRepository.save(entry);
        }
    }

    private MatchResultResponse toResponse(MatchResult result) {
        return MatchResultResponse.builder()
                .id(result.getId())
                .matchId(result.getMatch().getId())
                .winnerTeamId(result.getWinnerTeam() != null ? result.getWinnerTeam().getId() : null)
                .winnerTeamName(result.getWinnerTeam() != null ? result.getWinnerTeam().getName() : null)
                .tossWinnerTeamId(result.getTossWinnerTeam() != null ? result.getTossWinnerTeam().getId() : null)
                .tossWinnerTeamName(result.getTossWinnerTeam() != null ? result.getTossWinnerTeam().getName() : null)
                .playerOfMatchId(result.getPlayerOfMatch() != null ? result.getPlayerOfMatch().getId() : null)
                .playerOfMatchName(result.getPlayerOfMatch() != null ? result.getPlayerOfMatch().getName() : null)
                .isTie(result.getIsTie())
                .publishedAt(result.getPublishedAt())
                .publishedBy(result.getPublishedBy())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
}
