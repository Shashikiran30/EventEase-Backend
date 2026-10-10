package com.eventease.repository;

import com.eventease.entity.ApprovalWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, Long> {
    Optional<ApprovalWorkflow> findByName(String name);
    Optional<ApprovalWorkflow> findByEventType(String eventType);
}
