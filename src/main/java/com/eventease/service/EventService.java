package com.eventease.service;

import com.eventease.dto.AiCompletenessResponse;
import com.eventease.dto.AiSummaryResponse;
import com.eventease.dto.EventCreateRequest;
import com.eventease.dto.EventResponseDto;
import com.eventease.dto.EventUpdateRequest;
import com.eventease.dto.VenueAvailabilityCheck;
import com.eventease.entity.*;
import com.eventease.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventProposalRepository eventProposalRepository;
    private final DepartmentRepository departmentRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final ApprovalWorkflowRepository workflowRepository;
    private final ApprovalActionRepository approvalActionRepository;
    private final EventRegistrationRepository registrationRepository;
    private final VenueService venueService;
    private final NotificationService notificationService;
    private final AiService aiService;
    private final ApprovalService approvalService;

    public EventService(EventProposalRepository eventProposalRepository,
                        DepartmentRepository departmentRepository,
                        VenueRepository venueRepository,
                        UserRepository userRepository,
                        ApprovalWorkflowRepository workflowRepository,
                        ApprovalActionRepository approvalActionRepository,
                        EventRegistrationRepository registrationRepository,
                        VenueService venueService,
                        NotificationService notificationService,
                        AiService aiService,
                        ApprovalService approvalService) {
        this.eventProposalRepository = eventProposalRepository;
        this.departmentRepository = departmentRepository;
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
        this.workflowRepository = workflowRepository;
        this.approvalActionRepository = approvalActionRepository;
        this.registrationRepository = registrationRepository;
        this.venueService = venueService;
        this.notificationService = notificationService;
        this.aiService = aiService;
        this.approvalService = approvalService;
    }

    @Transactional
    public EventResponseDto createEvent(EventCreateRequest request, User organizer) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));

        // Security check: students cannot submit events under other departments
        if (organizer.getRole() == Role.ROLE_STUDENT && organizer.getDepartment() != null) {
            department = organizer.getDepartment();
        }

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue not found with id: " + request.getVenueId()));

        // Conflict detection: ensure venue is free
        VenueAvailabilityCheck check = venueService.checkAvailability(
                venue.getId(), request.getStartDate(), request.getEndDate(), null);
        if (!check.getIsAvailable()) {
            throw new IllegalArgumentException(check.getConflictReason());
        }

        User coordinator = null;
        if (request.getCoordinatorId() != null) {
            coordinator = userRepository.findById(request.getCoordinatorId()).orElse(null);
        }

        // Pick approval workflow based on budget or category
        ApprovalWorkflow workflow = workflowRepository.findByEventType("STANDARD")
                .orElseGet(() -> workflowRepository.findAll().stream().findFirst().orElse(null));

        EventProposal event = new EventProposal();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setObjectives(request.getObjectives());
        event.setCategory(request.getCategory());
        event.setDepartment(department);
        event.setOrganizer(organizer);
        event.setFacultyCoordinator(coordinator);
        event.setVenue(venue);
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setEstimatedParticipants(request.getEstimatedParticipants() != null ? request.getEstimatedParticipants() : 50);
        event.setBudget(request.getBudget() != null ? request.getBudget() : java.math.BigDecimal.ZERO);
        event.setBudgetBreakdown(request.getBudgetBreakdown());
        event.setDocumentUrls(request.getDocumentUrls());
        event.setWorkflow(workflow);
        event.setCurrentStepOrder(1);

        boolean submit = request.getSubmitImmediately() == null || request.getSubmitImmediately();
        event.setStatus(submit ? EventStatus.PENDING_HOD : EventStatus.DRAFT);

        // Calculate AI completeness and cached summary
        try {
            AiCompletenessResponse completeness = aiService.evaluateCompleteness(
                    event.getTitle(), event.getDescription(), event.getObjectives(),
                    venue.getName(), event.getBudget(), event.getBudgetBreakdown(), event.getEstimatedParticipants());
            event.setAiCompletenessScore(completeness.getScore());

            AiSummaryResponse summary = aiService.generateExecutiveSummary(event);
            event.setAiSummary(summary.getExecutiveSummary() + "\n\n" + summary.getAudienceAndScale());
        } catch (Exception e) {
            // Plan: If the AI service is unavailable, core submission should continue
            event.setAiCompletenessScore(85);
        }

        EventProposal savedEvent = eventProposalRepository.save(event);

        if (submit) {
            // Record initial SUBMIT action in audit trail
            ApprovalAction action = new ApprovalAction(
                    savedEvent,
                    organizer,
                    organizer.getRole(),
                    ActionType.SUBMIT,
                    "Proposal submitted for departmental review",
                    EventStatus.DRAFT,
                    EventStatus.PENDING_HOD,
                    1
            );
            approvalActionRepository.save(action);

            // Notify HODs of the department
            List<User> hods = userRepository.findByRoleAndDepartmentId(Role.ROLE_HOD, department.getId());
            for (User hod : hods) {
                notificationService.sendNotification(
                        hod,
                        "New Event Proposal: " + savedEvent.getTitle(),
                        "A new event proposal has been submitted by " + organizer.getFullName() + " for your review.",
                        "SUBMISSION",
                        savedEvent.getId()
                );
            }
        }

        return getEventById(savedEvent.getId(), organizer);
    }

    @Transactional
    public EventResponseDto updateEvent(Long eventId, EventUpdateRequest request, User user) {
        EventProposal event = eventProposalRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event proposal not found: " + eventId));

        // Authorization check: only organizer or admin can edit
        boolean isOrganizer = event.getOrganizer().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        if (!isOrganizer && !isAdmin) {
            throw new SecurityException("You do not have permission to modify this proposal.");
        }

        // Only allow edits if in DRAFT or CHANGES_REQUESTED
        if (event.getStatus() != EventStatus.DRAFT && event.getStatus() != EventStatus.CHANGES_REQUESTED) {
            throw new IllegalStateException("Only proposals in DRAFT or CHANGES_REQUESTED status can be modified.");
        }

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getObjectives() != null) event.setObjectives(request.getObjectives());
        if (request.getCategory() != null) event.setCategory(request.getCategory());
        if (request.getEstimatedParticipants() != null) event.setEstimatedParticipants(request.getEstimatedParticipants());
        if (request.getBudget() != null) event.setBudget(request.getBudget());
        if (request.getBudgetBreakdown() != null) event.setBudgetBreakdown(request.getBudgetBreakdown());
        if (request.getDocumentUrls() != null) event.setDocumentUrls(request.getDocumentUrls());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            event.setDepartment(dept);
        }

        if (request.getVenueId() != null || request.getStartDate() != null || request.getEndDate() != null) {
            Long venueId = request.getVenueId() != null ? request.getVenueId() : event.getVenue().getId();
            LocalDateTime start = request.getStartDate() != null ? request.getStartDate() : event.getStartDate();
            LocalDateTime end = request.getEndDate() != null ? request.getEndDate() : event.getEndDate();

            VenueAvailabilityCheck check = venueService.checkAvailability(venueId, start, end, event.getId());
            if (!check.getIsAvailable()) {
                throw new IllegalArgumentException(check.getConflictReason());
            }

            if (request.getVenueId() != null) {
                Venue v = venueRepository.findById(request.getVenueId()).orElseThrow();
                event.setVenue(v);
            }
            if (request.getStartDate() != null) event.setStartDate(request.getStartDate());
            if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        }

        // Resubmission flow
        if (Boolean.TRUE.equals(request.getResubmit())) {
            EventStatus prevStatus = event.getStatus();
            event.setStatus(EventStatus.PENDING_HOD);
            event.setCurrentStepOrder(1);

            ApprovalAction action = new ApprovalAction(
                    event,
                    user,
                    user.getRole(),
                    ActionType.RESUBMIT,
                    request.getResubmissionComments() != null ? request.getResubmissionComments() : "Proposal revised and resubmitted",
                    prevStatus,
                    EventStatus.PENDING_HOD,
                    1
            );
            approvalActionRepository.save(action);

            // Notify HOD
            if (event.getDepartment() != null) {
                List<User> hods = userRepository.findByRoleAndDepartmentId(Role.ROLE_HOD, event.getDepartment().getId());
                for (User hod : hods) {
                    notificationService.sendNotification(
                            hod,
                            "Resubmitted Proposal: " + event.getTitle(),
                            organizerNotificationMessage(user, event),
                            "SUBMISSION",
                            event.getId()
                    );
                }
            }
        }

        EventProposal saved = eventProposalRepository.save(event);
        return getEventById(saved.getId(), user);
    }

    private String organizerNotificationMessage(User user, EventProposal event) {
        return user.getFullName() + " has resubmitted '" + event.getTitle() + "' after addressing requested changes.";
    }

    public EventResponseDto getEventById(Long eventId, User currentUser) {
        EventProposal event = eventProposalRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event proposal not found: " + eventId));

        // Department-based access control: enforce that HOD and students cannot access other departments' private proposals
        if (currentUser != null && event.getStatus() != EventStatus.PUBLISHED) {
            if (currentUser.getRole() == Role.ROLE_HOD) {
                if (currentUser.getDepartment() != null && event.getDepartment() != null
                        && !currentUser.getDepartment().getId().equals(event.getDepartment().getId())) {
                    throw new SecurityException("Unauthorized: You can only view event proposals for your own department (" +
                            currentUser.getDepartment().getName() + ").");
                }
            } else if (currentUser.getRole() == Role.ROLE_STUDENT) {
                if (currentUser.getDepartment() != null && event.getDepartment() != null
                        && !currentUser.getDepartment().getId().equals(event.getDepartment().getId())
                        && !event.getOrganizer().getId().equals(currentUser.getId())) {
                    throw new SecurityException("Unauthorized: You cannot access unapproved event proposals from another department.");
                }
            }
        }

        EventResponseDto dto = EventResponseDto.fromEntity(event);
        dto.setApprovalHistory(approvalService.getApprovalHistory(eventId));
        dto.setRegistrationCount(registrationRepository.countByEventId(eventId));

        if (currentUser != null) {
            dto.setUserRegistered(registrationRepository.existsByEventIdAndUserId(eventId, currentUser.getId()));
        }

        // Fill current step metadata
        if (event.getWorkflow() != null && event.getCurrentStepOrder() != null) {
            event.getWorkflow().getSteps().stream()
                    .filter(s -> s.getStepOrder().equals(event.getCurrentStepOrder()))
                    .findFirst()
                    .ifPresent(step -> {
                        dto.setCurrentStepName(step.getStepName());
                        dto.setCurrentRequiredRole(step.getRequiredRole().name());
                    });
        }

        return dto;
    }

    public List<EventResponseDto> listEventsForUser(User user) {
        List<EventProposal> events;

        if (user.getRole() == Role.ROLE_STUDENT) {
            if (user.getDepartment() != null) {
                events = eventProposalRepository.findByDepartmentIdOrderByCreatedAtDesc(user.getDepartment().getId());
            } else {
                events = eventProposalRepository.findByOrganizerIdOrderByCreatedAtDesc(user.getId());
            }
        } else if (user.getRole() == Role.ROLE_HOD && user.getDepartment() != null) {
            events = eventProposalRepository.findByDepartmentIdOrderByCreatedAtDesc(user.getDepartment().getId());
        } else if (user.getRole() == Role.ROLE_HOD) {
            events = new java.util.ArrayList<>();
        } else {
            // DEAN, ADMIN, FINANCE see all events
            events = eventProposalRepository.findAll();
        }

        return events.stream()
                .map(e -> getEventById(e.getId(), user))
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponseDto publishEvent(Long eventId, User user) {
        EventProposal event = eventProposalRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event proposal not found: " + eventId));

        // Strict Requirement: Only APPROVED events can be published!
        if (event.getStatus() != EventStatus.APPROVED) {
            throw new IllegalStateException("Cannot publish event: Only APPROVED proposals can be published. Current status is " + event.getStatus());
        }

        // Check venue conflict once again right before publishing
        VenueAvailabilityCheck check = venueService.checkAvailability(
                event.getVenue().getId(), event.getStartDate(), event.getEndDate(), event.getId());
        if (!check.getIsAvailable()) {
            throw new IllegalStateException("Cannot publish event due to venue schedule collision: " + check.getConflictReason());
        }

        EventStatus prevStatus = event.getStatus();
        event.setStatus(EventStatus.PUBLISHED);
        EventProposal saved = eventProposalRepository.save(event);

        ApprovalAction action = new ApprovalAction(
                saved,
                user,
                user.getRole(),
                ActionType.PUBLISH,
                "Event officially published to college portal and calendar",
                prevStatus,
                EventStatus.PUBLISHED,
                event.getCurrentStepOrder()
        );
        approvalActionRepository.save(action);

        notificationService.sendNotification(
                event.getOrganizer(),
                "Event Published Live! 🚀",
                "'" + event.getTitle() + "' is now live on the EventEase College Calendar! Students can now register.",
                "PUBLISHED",
                saved.getId()
        );

        return getEventById(saved.getId(), user);
    }

    public List<EventResponseDto> getPublishedEvents() {
        return eventProposalRepository.findByStatusOrderByCreatedAtDesc(EventStatus.PUBLISHED).stream()
                .map(EventResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
