package com.familyleague.repository;

import com.familyleague.entity.LeaguePrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaguePredictionRepository extends JpaRepository<LeaguePrediction, UUID> {

    Optional<LeaguePrediction> findByUserIdAndSeasonId(UUID userId, UUID seasonId);

    Page<LeaguePrediction> findAllBySeasonId(UUID seasonId, Pageable pageable);

    List<LeaguePrediction> findAllBySeasonIdAndIsLockedFalse(UUID seasonId);
}
