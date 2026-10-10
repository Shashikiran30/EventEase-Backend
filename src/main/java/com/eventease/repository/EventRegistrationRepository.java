package com.eventease.repository;

import com.eventease.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEventId(Long eventId);
    List<EventRegistration> findByUserIdOrderByRegisteredAtDesc(Long userId);
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);
    Boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Optional<EventRegistration> findByTicketCode(String ticketCode);
    Long countByEventId(Long eventId);
}
