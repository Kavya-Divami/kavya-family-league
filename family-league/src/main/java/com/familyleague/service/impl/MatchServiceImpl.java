package com.familyleague.service.impl;

import com.familyleague.dto.request.CreateMatchRequest;
import com.familyleague.dto.request.UpdateMatchRequest;
import com.familyleague.dto.response.MatchResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.entity.Match;
import com.familyleague.entity.Season;
import com.familyleague.entity.Team;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.MatchRepository;
import com.familyleague.repository.SeasonRepository;
import com.familyleague.repository.TeamRepository;
import com.familyleague.service.MatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final SeasonRepository seasonRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public MatchResponse createMatch(CreateMatchRequest request) {
        log.debug("Creating match for seasonId={}", request.getSeasonId());
        Season season = seasonRepository.findByIdAndIsDeletedFalse(request.getSeasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Season", request.getSeasonId()));
        Team homeTeam = teamRepository.findByIdAndIsDeletedFalse(request.getHomeTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Home team", request.getHomeTeamId()));
        Team awayTeam = teamRepository.findByIdAndIsDeletedFalse(request.getAwayTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Away team", request.getAwayTeamId()));

        OffsetDateTime lockAt = request.getScheduledAt().minusHours(1);

        Match match = Match.builder()
                .season(season)
                .homeTeam(homeTeam)
                .awayTeam(awayTeam)
                .matchNumber(request.getMatchNumber())
                .venue(request.getVenue())
                .scheduledAt(request.getScheduledAt())
                .lockAt(lockAt)
                .build();
        Match saved = matchRepository.save(match);
        log.info("Created match id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public MatchResponse getMatchById(UUID id) {
        return toResponse(findMatch(id));
    }

    @Override
    public PagedResponse<MatchResponse> getMatchesBySeason(UUID seasonId, Pageable pageable) {
        seasonRepository.findByIdAndIsDeletedFalse(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season", seasonId));
        Page<Match> page = matchRepository.findAllBySeasonIdAndIsDeletedFalse(seasonId, pageable);
        return buildPagedResponse(page);
    }

    @Override
    public PagedResponse<MatchResponse> getAllMatches(int page, int size, String sortBy, String sortDir, UUID seasonId) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (seasonId != null) {
            return getMatchesBySeason(seasonId, pageable);
        }
        Page<Match> matchPage = matchRepository.findAll(pageable);
        return buildPagedResponse(matchPage);
    }

    @Override
    @Transactional
    public MatchResponse updateMatch(UUID id, UpdateMatchRequest request) {
        log.debug("Updating match id={}", id);
        Match match = findMatch(id);

        if (request.getHomeTeamId() != null) {
            Team homeTeam = teamRepository.findByIdAndIsDeletedFalse(request.getHomeTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Home team", request.getHomeTeamId()));
            match.setHomeTeam(homeTeam);
        }
        if (request.getAwayTeamId() != null) {
            Team awayTeam = teamRepository.findByIdAndIsDeletedFalse(request.getAwayTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Away team", request.getAwayTeamId()));
            match.setAwayTeam(awayTeam);
        }
        if (request.getScheduledAt() != null) {
            match.setScheduledAt(request.getScheduledAt());
            match.setLockAt(request.getScheduledAt().minusHours(1));
        }
        if (request.getMatchNumber() != null) match.setMatchNumber(request.getMatchNumber());
        if (request.getVenue() != null) match.setVenue(request.getVenue());

        Match saved = matchRepository.save(match);
        log.info("Updated match id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteMatch(UUID id) {
        log.debug("Soft-deleting match id={}", id);
        Match match = findMatch(id);
        match.setIsDeleted(true);
        matchRepository.save(match);
        log.info("Soft-deleted match id={}", id);
    }

    private Match findMatch(UUID id) {
        return matchRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
    }

    private MatchResponse toResponse(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .seasonId(match.getSeason().getId())
                .seasonNumber(match.getSeason().getSeasonNumber())
                .homeTeamId(match.getHomeTeam().getId())
                .homeTeamName(match.getHomeTeam().getName())
                .awayTeamId(match.getAwayTeam().getId())
                .awayTeamName(match.getAwayTeam().getName())
                .matchNumber(match.getMatchNumber())
                .venue(match.getVenue())
                .scheduledAt(match.getScheduledAt())
                .lockAt(match.getLockAt())
                .status(match.getStatus())
                .createdAt(match.getCreatedAt())
                .createdBy(match.getCreatedBy())
                .updatedAt(match.getUpdatedAt())
                .updatedBy(match.getUpdatedBy())
                .build();
    }

    private PagedResponse<MatchResponse> buildPagedResponse(Page<Match> page) {
        return PagedResponse.<MatchResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
