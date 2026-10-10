package com.eventease.repository;

import com.eventease.entity.ApprovalStep;
import com.eventease.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {
    List<ApprovalStep> findByWorkflowIdOrderByStepOrderAsc(Long workflowId);
    Optional<ApprovalStep> findByWorkflowIdAndStepOrder(Long workflowId, Integer stepOrder);
    List<ApprovalStep> findByRequiredRole(Role requiredRole);
}
