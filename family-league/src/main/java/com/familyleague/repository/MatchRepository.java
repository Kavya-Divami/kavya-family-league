package com.familyleague.repository;

import com.familyleague.entity.Match;
import com.familyleague.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    Optional<Match> findByIdAndIsDeletedFalse(UUID id);

    Page<Match> findAllBySeasonIdAndIsDeletedFalse(UUID seasonId, Pageable pageable);

    List<Match> findAllBySeasonIdAndStatusAndIsDeletedFalse(UUID seasonId, MatchStatus status);

    @Query("SELECT m FROM Match m WHERE m.isDeleted = FALSE AND m.status = 'SCHEDULED' AND m.lockAt BETWEEN :from AND :to")
    List<Match> findMatchesWithLockAtBetween(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    @Query("SELECT MIN(m.scheduledAt) FROM Match m WHERE m.season.id = :seasonId AND m.isDeleted = FALSE")
    Optional<OffsetDateTime> findFirstMatchScheduledAtBySeason(@Param("seasonId") UUID seasonId);
}
