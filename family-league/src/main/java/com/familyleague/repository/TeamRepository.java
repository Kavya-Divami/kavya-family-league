package com.familyleague.repository;

import com.familyleague.entity.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    Optional<Team> findByIdAndIsDeletedFalse(UUID id);

    Page<Team> findAllByIsDeletedFalse(Pageable pageable);

    boolean existsByNameAndIsDeletedFalse(String name);

    @Query("""
            SELECT t FROM Team t
            WHERE t.isDeleted = FALSE
              AND (:search IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(t.shortCode) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Team> searchTeams(@Param("search") String search, Pageable pageable);
}
