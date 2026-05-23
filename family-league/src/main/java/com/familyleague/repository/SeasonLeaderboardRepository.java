package com.familyleague.repository;

import com.familyleague.entity.SeasonLeaderboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeasonLeaderboardRepository extends JpaRepository<SeasonLeaderboard, UUID> {

    Optional<SeasonLeaderboard> findByUserIdAndSeasonId(UUID userId, UUID seasonId);

    Page<SeasonLeaderboard> findAllBySeasonIdOrderByRankAsc(UUID seasonId, Pageable pageable);

    List<SeasonLeaderboard> findAllBySeasonIdOrderByTotalPointsDesc(UUID seasonId);
}
