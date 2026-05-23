package com.familyleague.repository;

import com.familyleague.entity.EmailLog;
import com.familyleague.enums.EmailEventType;
import com.familyleague.enums.EmailStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {

    Page<EmailLog> findAllByRecipientUserId(UUID userId, Pageable pageable);

    Page<EmailLog> findAllByEventType(EmailEventType eventType, Pageable pageable);

    List<EmailLog> findAllByStatus(EmailStatus status);

    boolean existsByRecipientUserIdAndRelatedEntityIdAndEventType(UUID userId, UUID relatedEntityId, EmailEventType eventType);
}
