package com.familyleague.repository;

import com.familyleague.entity.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, UUID> {

    Optional<MatchResult> findByMatchId(UUID matchId);

    boolean existsByMatchId(UUID matchId);
}
