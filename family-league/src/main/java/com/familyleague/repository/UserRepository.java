package com.familyleague.repository;

import com.familyleague.entity.User;
import com.familyleague.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByIdAndIsDeletedFalse(UUID id);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    boolean existsByUsernameAndIsDeletedFalse(String username);

    boolean existsByEmailAndIsDeletedFalse(String email);

    Page<User> findAllByIsDeletedFalse(Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE u.isDeleted = FALSE
              AND (:role IS NULL OR u.role = :role)
              AND (:search IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(u.email)    LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                                   OR LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<User> searchUsers(@Param("search") String search,
                           @Param("role") Role role,
                           Pageable pageable);
}
