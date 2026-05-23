package com.familyleague.service.impl;

import com.familyleague.dto.response.LeaderboardEntryResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.entity.SeasonLeaderboard;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.SeasonLeaderboardRepository;
import com.familyleague.repository.SeasonRepository;
import com.familyleague.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeaderboardServiceImpl implements LeaderboardService {

    private final SeasonLeaderboardRepository seasonLeaderboardRepository;
    private final SeasonRepository seasonRepository;

    @Override
    public PagedResponse<LeaderboardEntryResponse> getLeaderboard(UUID seasonId, int page, int size) {
        log.debug("Getting leaderboard for seasonId={}", seasonId);
        seasonRepository.findByIdAndIsDeletedFalse(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season", seasonId));

        Page<SeasonLeaderboard> leaderboardPage = seasonLeaderboardRepository
                .findAllBySeasonIdOrderByRankAsc(seasonId, PageRequest.of(page, size));

        return PagedResponse.<LeaderboardEntryResponse>builder()
                .content(leaderboardPage.getContent().stream().map(this::toResponse).toList())
                .page(leaderboardPage.getNumber())
                .size(leaderboardPage.getSize())
                .totalElements(leaderboardPage.getTotalElements())
                .totalPages(leaderboardPage.getTotalPages())
                .last(leaderboardPage.isLast())
                .build();
    }

    private LeaderboardEntryResponse toResponse(SeasonLeaderboard entry) {
        return LeaderboardEntryResponse.builder()
                .rank(entry.getRank())
                .userId(entry.getUser().getId())
                .username(entry.getUser().getUsername())
                .totalPoints(entry.getTotalPoints())
                .build();
    }
}
