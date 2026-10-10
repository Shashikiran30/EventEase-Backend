package com.eventease.service;

import com.eventease.dto.ApprovalActionDto;
import com.eventease.dto.ApprovalDecisionRequest;
import com.eventease.dto.EventResponseDto;
import com.eventease.entity.*;
import com.eventease.repository.ApprovalActionRepository;
import com.eventease.repository.ApprovalStepRepository;
import com.eventease.repository.EventProposalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApprovalService {

    private final EventProposalRepository eventProposalRepository;
    private final ApprovalActionRepository approvalActionRepository;
    private final ApprovalStepRepository approvalStepRepository;
    private final NotificationService notificationService;

    public ApprovalService(EventProposalRepository eventProposalRepository,
                           ApprovalActionRepository approvalActionRepository,
                           ApprovalStepRepository approvalStepRepository,
                           NotificationService notificationService) {
        this.eventProposalRepository = eventProposalRepository;
        this.approvalActionRepository = approvalActionRepository;
        this.approvalStepRepository = approvalStepRepository;
        this.notificationService = notificationService;
    }

    public List<EventResponseDto> getPendingApprovalsForUser(User user) {
        List<EventProposal> proposals = new ArrayList<>();

        if (user.getRole() == Role.ROLE_HOD) {
            if (user.getDepartment() != null) {
                proposals = eventProposalRepository.findByDepartmentIdAndStatusOrderByCreatedAtDesc(
                        user.getDepartment().getId(), EventStatus.PENDING_HOD);
            } else {
                proposals = eventProposalRepository.findByStatusOrderByCreatedAtDesc(EventStatus.PENDING_HOD);
            }
        } else if (user.getRole() == Role.ROLE_DEAN) {
            proposals = eventProposalRepository.findByStatusOrderByCreatedAtDesc(EventStatus.PENDING_DEAN);
        } else if (user.getRole() == Role.ROLE_FINANCE_OFFICER) {
            proposals = eventProposalRepository.findByStatusOrderByCreatedAtDesc(EventStatus.PENDING_FINANCE);
        } else if (user.getRole() == Role.ROLE_PRINCIPAL) {
            proposals = eventProposalRepository.findByStatusOrderByCreatedAtDesc(EventStatus.PENDING_PRINCIPAL);
        } else if (user.getRole() == Role.ROLE_ADMIN) {
            proposals = eventProposalRepository.findByStatusInOrderByCreatedAtDesc(
                    List.of(EventStatus.PENDING_HOD, EventStatus.PENDING_DEAN,
                            EventStatus.PENDING_FINANCE, EventStatus.PENDING_PRINCIPAL));
        }

        return proposals.stream()
                .map(EventResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponseDto processDecision(Long eventId, ApprovalDecisionRequest decisionRequest, User actor) {
        EventProposal event = eventProposalRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event proposal not found with id: " + eventId));

        EventStatus currentStatus = event.getStatus();

        // 1. Validate that the event is actually in a reviewable state
        if (currentStatus != EventStatus.PENDING_HOD &&
            currentStatus != EventStatus.PENDING_DEAN &&
            currentStatus != EventStatus.PENDING_FINANCE &&
            currentStatus != EventStatus.PENDING_PRINCIPAL) {
            throw new IllegalStateException("Event is currently in status '" + currentStatus + "' and cannot receive approval decisions.");
        }

        // 2. Strict backend authorization checks
        // Rule: Dean cannot act before HOD step is complete
        if (currentStatus == EventStatus.PENDING_HOD) {
            if (actor.getRole() == Role.ROLE_DEAN) {
                throw new SecurityException("Unauthorized: Dean cannot review this proposal before mandatory HOD approval is completed.");
            }
            if (actor.getRole() != Role.ROLE_HOD && actor.getRole() != Role.ROLE_ADMIN) {
                throw new SecurityException("Unauthorized: Only an HOD or Administrator can approve at this stage.");
            }
            if (actor.getRole() == Role.ROLE_HOD && actor.getDepartment() != null && event.getDepartment() != null) {
                if (!actor.getDepartment().getId().equals(event.getDepartment().getId())) {
                    throw new SecurityException("Unauthorized: You can only review event proposals for your own department (" + 
                            actor.getDepartment().getName() + ").");
                }
            }
        } else if (currentStatus == EventStatus.PENDING_DEAN) {
            if (actor.getRole() == Role.ROLE_HOD) {
                throw new SecurityException("Unauthorized: HOD has already reviewed this proposal. Currently awaiting Dean review.");
            }
            if (actor.getRole() != Role.ROLE_DEAN && actor.getRole() != Role.ROLE_ADMIN) {
                throw new SecurityException("Unauthorized: Only the Dean or Administrator can review at this stage.");
            }
        } else if (currentStatus == EventStatus.PENDING_FINANCE) {
            if (actor.getRole() != Role.ROLE_FINANCE_OFFICER && actor.getRole() != Role.ROLE_ADMIN) {
                throw new SecurityException("Unauthorized: Only the Finance Officer or Administrator can review at this stage.");
            }
        } else if (currentStatus == EventStatus.PENDING_PRINCIPAL) {
            if (actor.getRole() != Role.ROLE_PRINCIPAL && actor.getRole() != Role.ROLE_ADMIN) {
                throw new SecurityException("Unauthorized: Only the Principal or Administrator can review at this stage.");
            }
        }

        EventStatus nextStatus = currentStatus;
        ActionType action = decisionRequest.getAction();
        Integer currentStep = event.getCurrentStepOrder() != null ? event.getCurrentStepOrder() : 1;

        if (action == ActionType.APPROVE) {
            // Check workflow steps
            List<ApprovalStep> steps = List.of();
            if (event.getWorkflow() != null) {
                steps = approvalStepRepository.findByWorkflowIdOrderByStepOrderAsc(event.getWorkflow().getId());
            }

            int totalSteps = steps.isEmpty() ? 2 : steps.size();

            if (currentStep < totalSteps) {
                // Advance to next step
                int nextStepOrder = currentStep + 1;
                event.setCurrentStepOrder(nextStepOrder);

                ApprovalStep nextStep = steps.stream()
                        .filter(s -> s.getStepOrder().equals(nextStepOrder))
                        .findFirst()
                        .orElse(null);

                if (nextStep != null) {
                    if (nextStep.getRequiredRole() == Role.ROLE_DEAN) {
                        nextStatus = EventStatus.PENDING_DEAN;
                    } else if (nextStep.getRequiredRole() == Role.ROLE_FINANCE_OFFICER) {
                        nextStatus = EventStatus.PENDING_FINANCE;
                    } else if (nextStep.getRequiredRole() == Role.ROLE_PRINCIPAL) {
                        nextStatus = EventStatus.PENDING_PRINCIPAL;
                    } else {
                        nextStatus = EventStatus.PENDING_DEAN;
                    }
                } else {
                    // Default 2-step fallback: 1=HOD, 2=DEAN
                    if (nextStepOrder == 2) {
                        nextStatus = EventStatus.PENDING_DEAN;
                    } else {
                        nextStatus = EventStatus.APPROVED;
                    }
                }

                // Notify organizer of intermediate progress
                notificationService.sendNotification(
                        event.getOrganizer(),
                        "Proposal Step Approved: " + event.getTitle(),
                        "Your proposal was approved by " + actor.getFullName() + " (" + actor.getRole() + 
                                ") and forwarded to the next approval authority.",
                        "DECISION",
                        event.getId()
                );
            } else {
                // Final step approved -> Event is officially APPROVED!
                nextStatus = EventStatus.APPROVED;

                notificationService.sendNotification(
                        event.getOrganizer(),
                        "Proposal Fully Approved! 🎉",
                        "Congratulations! All mandatory approval levels for '" + event.getTitle() + 
                                "' have been completed. Your event is approved and eligible for publication.",
                        "DECISION",
                        event.getId()
                );
            }
        } else if (action == ActionType.REJECT) {
            nextStatus = EventStatus.REJECTED;

            notificationService.sendNotification(
                    event.getOrganizer(),
                    "Proposal Rejected: " + event.getTitle(),
                    "Your proposal was rejected by " + actor.getFullName() + ". Reason: " + decisionRequest.getComments(),
                    "DECISION",
                    event.getId()
            );
        } else if (action == ActionType.REQUEST_CHANGES) {
            nextStatus = EventStatus.CHANGES_REQUESTED;

            notificationService.sendNotification(
                    event.getOrganizer(),
                    "Changes Requested: " + event.getTitle(),
                    actor.getFullName() + " has requested modifications on your event proposal. Feedback: " + 
                            decisionRequest.getComments(),
                    "CHANGES_REQUESTED",
                    event.getId()
            );
        } else {
            throw new IllegalArgumentException("Unsupported approval action: " + action);
        }

        // Apply state change
        event.setStatus(nextStatus);
        EventProposal savedEvent = eventProposalRepository.save(event);

        // Record immutable audit history
        ApprovalAction auditRecord = new ApprovalAction(
                savedEvent,
                actor,
                actor.getRole(),
                action,
                decisionRequest.getComments(),
                currentStatus,
                nextStatus,
                currentStep
        );
        approvalActionRepository.save(auditRecord);

        EventResponseDto dto = EventResponseDto.fromEntity(savedEvent);
        dto.setApprovalHistory(getApprovalHistory(savedEvent.getId()));
        return dto;
    }

    public List<ApprovalActionDto> getApprovalHistory(Long eventId) {
        return approvalActionRepository.findByEventProposalIdOrderByTimestampAsc(eventId).stream()
                .map(ApprovalActionDto::fromEntity)
                .collect(Collectors.toList());
    }
}
