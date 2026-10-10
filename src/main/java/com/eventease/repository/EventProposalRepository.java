package com.eventease.repository;

import com.eventease.entity.EventProposal;
import com.eventease.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventProposalRepository extends JpaRepository<EventProposal, Long> {

    List<EventProposal> findByOrganizerIdOrderByCreatedAtDesc(Long organizerId);

    List<EventProposal> findByDepartmentIdOrderByCreatedAtDesc(Long departmentId);

    List<EventProposal> findByStatusOrderByCreatedAtDesc(EventStatus status);

    List<EventProposal> findByStatusInOrderByCreatedAtDesc(List<EventStatus> statuses);

    List<EventProposal> findByDepartmentIdAndStatusOrderByCreatedAtDesc(Long departmentId, EventStatus status);

    // Conflict detection query: checks if venue is occupied during the given interval
    // Excludes CANCELLED, REJECTED, and DRAFT events, and excludes the current event if updating
    @Query("SELECT e FROM EventProposal e WHERE e.venue.id = :venueId " +
           "AND e.status NOT IN (com.eventease.entity.EventStatus.REJECTED, com.eventease.entity.EventStatus.CANCELLED, com.eventease.entity.EventStatus.DRAFT) " +
           "AND (:excludeEventId IS NULL OR e.id != :excludeEventId) " +
           "AND ((e.startDate < :endDate AND e.endDate > :startDate))")
    List<EventProposal> findConflictingEvents(
            @Param("venueId") Long venueId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludeEventId") Long excludeEventId
    );

    @Query("SELECT COUNT(e) FROM EventProposal e WHERE e.status = :status")
    Long countByStatus(@Param("status") EventStatus status);
}
