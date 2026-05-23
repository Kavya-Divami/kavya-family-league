package com.familyleague.service.impl;

import com.familyleague.dto.request.CreateTeamRequest;
import com.familyleague.dto.request.UpdateTeamRequest;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.TeamResponse;
import com.familyleague.entity.Team;
import com.familyleague.exception.DuplicateResourceException;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.TeamRepository;
import com.familyleague.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public TeamResponse createTeam(CreateTeamRequest request) {
        log.debug("Creating team with name: {}", request.getName());
        if (teamRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new DuplicateResourceException("Team with name '" + request.getName() + "' already exists");
        }
        Team team = Team.builder()
                .name(request.getName())
                .shortCode(request.getShortCode())
                .logoUrl(request.getLogoUrl())
                .build();
        Team saved = teamRepository.save(team);
        log.info("Created team id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public TeamResponse getTeamById(UUID id) {
        return toResponse(findTeam(id));
    }

    @Override
    public PagedResponse<TeamResponse> getAllTeams(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Team> teamPage;
        if (search != null && !search.isBlank()) {
            teamPage = teamRepository.searchTeams(search, pageable);
        } else {
            teamPage = teamRepository.findAllByIsDeletedFalse(pageable);
        }

        return PagedResponse.<TeamResponse>builder()
                .content(teamPage.getContent().stream().map(this::toResponse).toList())
                .page(teamPage.getNumber())
                .size(teamPage.getSize())
                .totalElements(teamPage.getTotalElements())
                .totalPages(teamPage.getTotalPages())
                .last(teamPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(UUID id, UpdateTeamRequest request) {
        log.debug("Updating team id={}", id);
        Team team = findTeam(id);

        if (request.getName() != null) {
            if (!team.getName().equals(request.getName())
                    && teamRepository.existsByNameAndIsDeletedFalse(request.getName())) {
                throw new DuplicateResourceException("Team with name '" + request.getName() + "' already exists");
            }
            team.setName(request.getName());
        }
        if (request.getShortCode() != null) team.setShortCode(request.getShortCode());
        if (request.getLogoUrl() != null) team.setLogoUrl(request.getLogoUrl());

        Team saved = teamRepository.save(team);
        log.info("Updated team id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteTeam(UUID id) {
        log.debug("Soft-deleting team id={}", id);
        Team team = findTeam(id);
        team.setIsDeleted(true);
        teamRepository.save(team);
        log.info("Soft-deleted team id={}", id);
    }

    private Team findTeam(UUID id) {
        return teamRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
    }

    public TeamResponse toResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .shortCode(team.getShortCode())
                .logoUrl(team.getLogoUrl())
                .createdAt(team.getCreatedAt())
                .createdBy(team.getCreatedBy())
                .updatedAt(team.getUpdatedAt())
                .updatedBy(team.getUpdatedBy())
                .build();
    }
}
