package com.familyleague.repository;

import com.familyleague.entity.League;
import com.familyleague.enums.LeagueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeagueRepository extends JpaRepository<League, UUID> {

    Optional<League> findByIdAndIsDeletedFalse(UUID id);

    Page<League> findAllByIsDeletedFalse(Pageable pageable);

    Page<League> findAllByStatusAndIsDeletedFalse(LeagueStatus status, Pageable pageable);

    boolean existsByNameAndIsDeletedFalse(String name);

    @Query("""
            SELECT l FROM League l
            WHERE l.isDeleted = FALSE
              AND (:search IS NULL OR LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(l.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<League> searchLeagues(@Param("search") String search, Pageable pageable);
}
