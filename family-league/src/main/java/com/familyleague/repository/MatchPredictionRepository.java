package com.familyleague.repository;

import com.familyleague.entity.MatchPrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchPredictionRepository extends JpaRepository<MatchPrediction, UUID> {

    Optional<MatchPrediction> findByUserIdAndMatchId(UUID userId, UUID matchId);

    Page<MatchPrediction> findAllByMatchId(UUID matchId, Pageable pageable);

    List<MatchPrediction> findAllByMatchIdAndIsLockedTrue(UUID matchId);

    List<MatchPrediction> findAllByMatchIdAndIsLockedFalse(UUID matchId);

    List<MatchPrediction> findAllByUserIdAndMatch_SeasonId(UUID userId, UUID seasonId);
}
