package com.eventease.dto;

import com.eventease.entity.EventProposal;
import com.eventease.entity.EventStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EventResponseDto {
    private Long id;
    private String title;
    private String description;
    private String objectives;
    private String category;
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private Long organizerId;
    private String organizerName;
    private String organizerEmail;
    private Long coordinatorId;
    private String coordinatorName;
    private Long venueId;
    private String venueName;
    private String venueLocation;
    private Integer venueCapacity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer estimatedParticipants;
    private BigDecimal budget;
    private String budgetBreakdown;
    private String documentUrls;
    private EventStatus status;
    private Long workflowId;
    private String workflowName;
    private Integer currentStepOrder;
    private String currentStepName;
    private String currentRequiredRole;
    private String aiSummary;
    private Integer aiCompletenessScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long registrationCount;
    private Boolean userRegistered = false;
    private List<ApprovalActionDto> approvalHistory;

    public EventResponseDto() {}

    public static EventResponseDto fromEntity(EventProposal event) {
        if (event == null) return null;
        EventResponseDto dto = new EventResponseDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setObjectives(event.getObjectives());
        dto.setCategory(event.getCategory());
        if (event.getDepartment() != null) {
            dto.setDepartmentId(event.getDepartment().getId());
            dto.setDepartmentName(event.getDepartment().getName());
            dto.setDepartmentCode(event.getDepartment().getCode());
        }
        if (event.getOrganizer() != null) {
            dto.setOrganizerId(event.getOrganizer().getId());
            dto.setOrganizerName(event.getOrganizer().getFullName());
            dto.setOrganizerEmail(event.getOrganizer().getEmail());
        }
        if (event.getFacultyCoordinator() != null) {
            dto.setCoordinatorId(event.getFacultyCoordinator().getId());
            dto.setCoordinatorName(event.getFacultyCoordinator().getFullName());
        }
        if (event.getVenue() != null) {
            dto.setVenueId(event.getVenue().getId());
            dto.setVenueName(event.getVenue().getName());
            dto.setVenueLocation(event.getVenue().getLocation());
            dto.setVenueCapacity(event.getVenue().getCapacity());
        }
        dto.setStartDate(event.getStartDate());
        dto.setEndDate(event.getEndDate());
        dto.setEstimatedParticipants(event.getEstimatedParticipants());
        dto.setBudget(event.getBudget());
        dto.setBudgetBreakdown(event.getBudgetBreakdown());
        dto.setDocumentUrls(event.getDocumentUrls());
        dto.setStatus(event.getStatus());
        if (event.getWorkflow() != null) {
            dto.setWorkflowId(event.getWorkflow().getId());
            dto.setWorkflowName(event.getWorkflow().getName());
        }
        dto.setCurrentStepOrder(event.getCurrentStepOrder());
        dto.setAiSummary(event.getAiSummary());
        dto.setAiCompletenessScore(event.getAiCompletenessScore());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }

    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueLocation() {
        return venueLocation;
    }

    public void setVenueLocation(String venueLocation) {
        this.venueLocation = venueLocation;
    }

    public Integer getVenueCapacity() {
        return venueCapacity;
    }

    public void setVenueCapacity(Integer venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getEstimatedParticipants() {
        return estimatedParticipants;
    }

    public void setEstimatedParticipants(Integer estimatedParticipants) {
        this.estimatedParticipants = estimatedParticipants;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getBudgetBreakdown() {
        return budgetBreakdown;
    }

    public void setBudgetBreakdown(String budgetBreakdown) {
        this.budgetBreakdown = budgetBreakdown;
    }

    public String getDocumentUrls() {
        return documentUrls;
    }

    public void setDocumentUrls(String documentUrls) {
        this.documentUrls = documentUrls;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public Integer getCurrentStepOrder() {
        return currentStepOrder;
    }

    public void setCurrentStepOrder(Integer currentStepOrder) {
        this.currentStepOrder = currentStepOrder;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    public String getCurrentRequiredRole() {
        return currentRequiredRole;
    }

    public void setCurrentRequiredRole(String currentRequiredRole) {
        this.currentRequiredRole = currentRequiredRole;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public Integer getAiCompletenessScore() {
        return aiCompletenessScore;
    }

    public void setAiCompletenessScore(Integer aiCompletenessScore) {
        this.aiCompletenessScore = aiCompletenessScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(Long registrationCount) {
        this.registrationCount = registrationCount;
    }

    public Boolean getUserRegistered() {
        return userRegistered;
    }

    public void setUserRegistered(Boolean userRegistered) {
        this.userRegistered = userRegistered;
    }

    public List<ApprovalActionDto> getApprovalHistory() {
        return approvalHistory;
    }

    public void setApprovalHistory(List<ApprovalActionDto> approvalHistory) {
        this.approvalHistory = approvalHistory;
    }
}
