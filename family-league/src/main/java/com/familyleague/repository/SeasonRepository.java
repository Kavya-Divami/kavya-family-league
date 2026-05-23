package com.familyleague.repository;

import com.familyleague.entity.Season;
import com.familyleague.enums.SeasonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeasonRepository extends JpaRepository<Season, UUID> {

    Optional<Season> findByIdAndIsDeletedFalse(UUID id);

    Page<Season> findAllByLeagueIdAndIsDeletedFalse(UUID leagueId, Pageable pageable);

    List<Season> findAllByStatusAndIsDeletedFalse(SeasonStatus status);

    boolean existsByLeagueIdAndSeasonNumberAndIsDeletedFalse(UUID leagueId, int seasonNumber);
}
