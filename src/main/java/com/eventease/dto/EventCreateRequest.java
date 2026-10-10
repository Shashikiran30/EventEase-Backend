package com.eventease.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String objectives;

    @NotBlank(message = "Category is required")
    private String category; // Technical, Workshop, Cultural, Sports, Seminar, Conference

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private Long coordinatorId;

    @NotNull(message = "Venue ID is required")
    private Long venueId;

    @NotNull(message = "Start date/time is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date/time is required")
    private LocalDateTime endDate;

    private Integer estimatedParticipants;

    private BigDecimal budget;

    private String budgetBreakdown;

    private String documentUrls;

    private Boolean submitImmediately = true; // If false, saved as DRAFT

    public EventCreateRequest() {}

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

    public Long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(Long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
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

    public Boolean getSubmitImmediately() {
        return submitImmediately;
    }

    public void setSubmitImmediately(Boolean submitImmediately) {
        this.submitImmediately = submitImmediately;
    }
}
