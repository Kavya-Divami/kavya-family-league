package com.familyleague.service.impl;

import com.familyleague.dto.request.CreateLeagueRequest;
import com.familyleague.dto.request.UpdateLeagueRequest;
import com.familyleague.dto.response.LeagueResponse;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.entity.League;
import com.familyleague.enums.LeagueStatus;
import com.familyleague.exception.DuplicateResourceException;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.LeagueRepository;
import com.familyleague.service.LeagueService;
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
public class LeagueServiceImpl implements LeagueService {

    private final LeagueRepository leagueRepository;

    @Override
    @Transactional
    public LeagueResponse createLeague(CreateLeagueRequest request) {
        log.debug("Creating league with name: {}", request.getName());
        if (leagueRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new DuplicateResourceException("League with name '" + request.getName() + "' already exists");
        }
        League league = League.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        League saved = leagueRepository.save(league);
        log.info("Created league id={} name={}", saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public LeagueResponse getLeagueById(UUID id) {
        return toResponse(findLeague(id));
    }

    @Override
    public PagedResponse<LeagueResponse> getAllLeagues(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<League> leaguePage;
        if (search != null && !search.isBlank()) {
            leaguePage = leagueRepository.searchLeagues(search, pageable);
        } else {
            leaguePage = leagueRepository.findAllByIsDeletedFalse(pageable);
        }

        return PagedResponse.<LeagueResponse>builder()
                .content(leaguePage.getContent().stream().map(this::toResponse).toList())
                .page(leaguePage.getNumber())
                .size(leaguePage.getSize())
                .totalElements(leaguePage.getTotalElements())
                .totalPages(leaguePage.getTotalPages())
                .last(leaguePage.isLast())
                .build();
    }

    @Override
    @Transactional
    public LeagueResponse updateLeague(UUID id, UpdateLeagueRequest request) {
        log.debug("Updating league id={}", id);
        League league = findLeague(id);

        if (request.getName() != null) {
            if (!league.getName().equals(request.getName())
                    && leagueRepository.existsByNameAndIsDeletedFalse(request.getName())) {
                throw new DuplicateResourceException("League with name '" + request.getName() + "' already exists");
            }
            league.setName(request.getName());
        }
        if (request.getDescription() != null) {
            league.setDescription(request.getDescription());
        }

        League saved = leagueRepository.save(league);
        log.info("Updated league id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void closeLeague(UUID id) {
        log.debug("Closing league id={}", id);
        League league = findLeague(id);
        league.setStatus(LeagueStatus.CLOSED);
        leagueRepository.save(league);
        log.info("Closed league id={}", id);
    }

    @Override
    @Transactional
    public void deleteLeague(UUID id) {
        log.debug("Soft-deleting league id={}", id);
        League league = findLeague(id);
        league.setIsDeleted(true);
        leagueRepository.save(league);
        log.info("Soft-deleted league id={}", id);
    }

    private League findLeague(UUID id) {
        return leagueRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("League", id));
    }

    private LeagueResponse toResponse(League league) {
        return LeagueResponse.builder()
                .id(league.getId())
                .name(league.getName())
                .description(league.getDescription())
                .status(league.getStatus())
                .createdAt(league.getCreatedAt())
                .createdBy(league.getCreatedBy())
                .updatedAt(league.getUpdatedAt())
                .updatedBy(league.getUpdatedBy())
                .build();
    }
}
