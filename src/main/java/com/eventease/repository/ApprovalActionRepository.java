package com.eventease.repository;

import com.eventease.entity.ApprovalAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalActionRepository extends JpaRepository<ApprovalAction, Long> {
    List<ApprovalAction> findByEventProposalIdOrderByTimestampAsc(Long eventId);
    List<ApprovalAction> findByActorIdOrderByTimestampDesc(Long actorId);
}
