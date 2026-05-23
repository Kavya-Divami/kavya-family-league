package com.familyleague.repository;

import com.familyleague.entity.SeasonTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeasonTeamRepository extends JpaRepository<SeasonTeam, UUID> {

    List<SeasonTeam> findAllBySeasonId(UUID seasonId);

    boolean existsBySeasonIdAndTeamId(UUID seasonId, UUID teamId);

    Optional<SeasonTeam> findBySeasonIdAndTeamId(UUID seasonId, UUID teamId);
}
