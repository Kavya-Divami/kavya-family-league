package com.familyleague.service.impl;

import com.familyleague.dto.request.CreateSeasonRequest;
import com.familyleague.dto.request.UpdateSeasonRequest;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.SeasonResponse;
import com.familyleague.dto.response.TeamResponse;
import com.familyleague.entity.League;
import com.familyleague.entity.Season;
import com.familyleague.entity.SeasonTeam;
import com.familyleague.entity.Team;
import com.familyleague.enums.SeasonStatus;
import com.familyleague.exception.DuplicateResourceException;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.LeagueRepository;
import com.familyleague.repository.SeasonRepository;
import com.familyleague.repository.SeasonTeamRepository;
import com.familyleague.repository.TeamRepository;
import com.familyleague.service.SeasonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final SeasonTeamRepository seasonTeamRepository;

    @Override
    @Transactional
    public SeasonResponse createSeason(CreateSeasonRequest request) {
        log.debug("Creating season for leagueId={} seasonNumber={}", request.getLeagueId(), request.getSeasonNumber());
        League league = leagueRepository.findByIdAndIsDeletedFalse(request.getLeagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League", request.getLeagueId()));

        if (seasonRepository.existsByLeagueIdAndSeasonNumberAndIsDeletedFalse(
                request.getLeagueId(), request.getSeasonNumber())) {
            throw new DuplicateResourceException("Season " + request.getSeasonNumber()
                    + " already exists for this league");
        }

        Season season = Season.builder()
                .league(league)
                .seasonNumber(request.getSeasonNumber())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .firstMatchStartsAt(request.getFirstMatchStartsAt())
                .leaguePredictionLockAt(request.getLeaguePredictionLockAt())
                .build();
        Season saved = seasonRepository.save(season);
        log.info("Created season id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    public SeasonResponse getSeasonById(UUID id) {
        return toResponse(findSeason(id));
    }

    @Override
    public PagedResponse<SeasonResponse> getSeasonsByLeague(UUID leagueId, Pageable pageable) {
        leagueRepository.findByIdAndIsDeletedFalse(leagueId)
                .orElseThrow(() -> new ResourceNotFoundException("League", leagueId));
        Page<Season> page = seasonRepository.findAllByLeagueIdAndIsDeletedFalse(leagueId, pageable);
        return buildPagedResponse(page);
    }

    @Override
    public PagedResponse<SeasonResponse> getAllSeasons(int page, int size, String sortBy, String sortDir, UUID leagueId) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (leagueId != null) {
            return getSeasonsByLeague(leagueId, pageable);
        }
        Page<Season> seasonPage = seasonRepository.findAll(pageable);
        return buildPagedResponse(seasonPage);
    }

    @Override
    @Transactional
    public SeasonResponse updateSeason(UUID id, UpdateSeasonRequest request) {
        log.debug("Updating season id={}", id);
        Season season = findSeason(id);

        if (request.getSeasonNumber() != null) season.setSeasonNumber(request.getSeasonNumber());
        if (request.getStartDate() != null) season.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) season.setEndDate(request.getEndDate());
        if (request.getFirstMatchStartsAt() != null) season.setFirstMatchStartsAt(request.getFirstMatchStartsAt());
        if (request.getLeaguePredictionLockAt() != null) season.setLeaguePredictionLockAt(request.getLeaguePredictionLockAt());

        Season saved = seasonRepository.save(season);
        log.info("Updated season id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void activateSeason(UUID id) {
        log.debug("Activating season id={}", id);
        Season season = findSeason(id);
        season.setStatus(SeasonStatus.ACTIVE);
        seasonRepository.save(season);
        log.info("Activated season id={}", id);
    }

    @Override
    @Transactional
    public void completeSeason(UUID id) {
        log.debug("Completing season id={}", id);
        Season season = findSeason(id);
        season.setStatus(SeasonStatus.COMPLETED);
        seasonRepository.save(season);
        log.info("Completed season id={}", id);
    }

    @Override
    @Transactional
    public void closeSeason(UUID id) {
        log.debug("Closing season id={}", id);
        Season season = findSeason(id);
        season.setStatus(SeasonStatus.CLOSED);
        seasonRepository.save(season);
        log.info("Closed season id={}", id);
    }

    @Override
    @Transactional
    public void addTeamToSeason(UUID seasonId, UUID teamId) {
        log.debug("Adding team {} to season {}", teamId, seasonId);
        Season season = findSeason(seasonId);
        Team team = teamRepository.findByIdAndIsDeletedFalse(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", teamId));

        if (seasonTeamRepository.existsBySeasonIdAndTeamId(seasonId, teamId)) {
            throw new DuplicateResourceException("Team is already added to this season");
        }

        SeasonTeam seasonTeam = SeasonTeam.builder()
                .season(season)
                .team(team)
                .build();
        seasonTeamRepository.save(seasonTeam);
        log.info("Added team {} to season {}", teamId, seasonId);
    }

    @Override
    public List<TeamResponse> getTeamsForSeason(UUID seasonId) {
        findSeason(seasonId);
        return seasonTeamRepository.findAllBySeasonId(seasonId).stream()
                .map(st -> TeamResponse.builder()
                        .id(st.getTeam().getId())
                        .name(st.getTeam().getName())
                        .shortCode(st.getTeam().getShortCode())
                        .logoUrl(st.getTeam().getLogoUrl())
                        .createdAt(st.getTeam().getCreatedAt())
                        .createdBy(st.getTeam().getCreatedBy())
                        .updatedAt(st.getTeam().getUpdatedAt())
                        .updatedBy(st.getTeam().getUpdatedBy())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void removeTeamFromSeason(UUID seasonId, UUID teamId) {
        log.debug("Removing team {} from season {}", teamId, seasonId);
        SeasonTeam seasonTeam = seasonTeamRepository.findBySeasonIdAndTeamId(seasonId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team is not part of this season"));
        seasonTeamRepository.delete(seasonTeam);
        log.info("Removed team {} from season {}", teamId, seasonId);
    }

    private Season findSeason(UUID id) {
        return seasonRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Season", id));
    }

    private SeasonResponse toResponse(Season season) {
        return SeasonResponse.builder()
                .id(season.getId())
                .leagueId(season.getLeague().getId())
                .leagueName(season.getLeague().getName())
                .seasonNumber(season.getSeasonNumber())
                .startDate(season.getStartDate())
                .endDate(season.getEndDate())
                .firstMatchStartsAt(season.getFirstMatchStartsAt())
                .leaguePredictionLockAt(season.getLeaguePredictionLockAt())
                .status(season.getStatus())
                .createdAt(season.getCreatedAt())
                .createdBy(season.getCreatedBy())
                .updatedAt(season.getUpdatedAt())
                .updatedBy(season.getUpdatedBy())
                .build();
    }

    private PagedResponse<SeasonResponse> buildPagedResponse(Page<Season> page) {
        return PagedResponse.<SeasonResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
