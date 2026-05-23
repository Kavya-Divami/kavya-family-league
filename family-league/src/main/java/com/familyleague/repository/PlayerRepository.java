package com.familyleague.repository;

import com.familyleague.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByIdAndIsDeletedFalse(UUID id);

    Page<Player> findAllByTeamIdAndIsDeletedFalse(UUID teamId, Pageable pageable);

    List<Player> findAllByTeamIdAndIsDeletedFalse(UUID teamId);
}
