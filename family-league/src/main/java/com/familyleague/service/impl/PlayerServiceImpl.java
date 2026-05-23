package com.familyleague.service.impl;

import com.familyleague.dto.request.CreatePlayerRequest;
import com.familyleague.dto.request.UpdatePlayerRequest;
import com.familyleague.dto.response.PagedResponse;
import com.familyleague.dto.response.PlayerResponse;
import com.familyleague.entity.Player;
import com.familyleague.entity.Team;
import com.familyleague.exception.ResourceNotFoundException;
import com.familyleague.repository.PlayerRepository;
import com.familyleague.repository.TeamRepository;
import com.familyleague.service.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public PlayerResponse createPlayer(UUID teamId, CreatePlayerRequest request) {
        log.debug("Creating player for teamId={}", teamId);
        Team team = teamRepository.findByIdAndIsDeletedFalse(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", teamId));

        Player player = Player.builder()
                .team(team)
                .name(request.getName())
                .jerseyNumber(request.getJerseyNumber())
                .build();
        Player saved = playerRepository.save(player);
        log.info("Created player id={} teamId={}", saved.getId(), teamId);
        return toResponse(saved);
    }

    @Override
    public PlayerResponse getPlayerById(UUID id) {
        return toResponse(findPlayer(id));
    }

    @Override
    public PagedResponse<PlayerResponse> getPlayersByTeam(UUID teamId, Pageable pageable) {
        teamRepository.findByIdAndIsDeletedFalse(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team", teamId));

        Page<Player> page = playerRepository.findAllByTeamIdAndIsDeletedFalse(teamId, pageable);
        return PagedResponse.<PlayerResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public PlayerResponse updatePlayer(UUID id, UpdatePlayerRequest request) {
        log.debug("Updating player id={}", id);
        Player player = findPlayer(id);

        if (request.getName() != null) player.setName(request.getName());
        if (request.getJerseyNumber() != null) player.setJerseyNumber(request.getJerseyNumber());

        Player saved = playerRepository.save(player);
        log.info("Updated player id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deletePlayer(UUID id) {
        log.debug("Soft-deleting player id={}", id);
        Player player = findPlayer(id);
        player.setIsDeleted(true);
        playerRepository.save(player);
        log.info("Soft-deleted player id={}", id);
    }

    private Player findPlayer(UUID id) {
        return playerRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player", id));
    }

    private PlayerResponse toResponse(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .teamId(player.getTeam().getId())
                .teamName(player.getTeam().getName())
                .name(player.getName())
                .jerseyNumber(player.getJerseyNumber())
                .createdAt(player.getCreatedAt())
                .createdBy(player.getCreatedBy())
                .updatedAt(player.getUpdatedAt())
                .updatedBy(player.getUpdatedBy())
                .build();
    }
}
