package com.eventease.dto;

import com.eventease.entity.ActionType;
import com.eventease.entity.ApprovalAction;
import com.eventease.entity.EventStatus;
import com.eventease.entity.Role;
import java.time.LocalDateTime;

public class ApprovalActionDto {
    private Long id;
    private Long eventId;
    private Long actorId;
    private String actorName;
    private Role actorRole;
    private ActionType action;
    private String comments;
    private EventStatus previousStatus;
    private EventStatus newStatus;
    private Integer stepOrder;
    private LocalDateTime timestamp;

    public ApprovalActionDto() {}

    public static ApprovalActionDto fromEntity(ApprovalAction action) {
        if (action == null) return null;
        ApprovalActionDto dto = new ApprovalActionDto();
        dto.setId(action.getId());
        if (action.getEventProposal() != null) {
            dto.setEventId(action.getEventProposal().getId());
        }
        if (action.getActor() != null) {
            dto.setActorId(action.getActor().getId());
            dto.setActorName(action.getActor().getFullName());
        }
        dto.setActorRole(action.getActorRole());
        dto.setAction(action.getAction());
        dto.setComments(action.getComments());
        dto.setPreviousStatus(action.getPreviousStatus());
        dto.setNewStatus(action.getNewStatus());
        dto.setStepOrder(action.getStepOrder());
        dto.setTimestamp(action.getTimestamp());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public Role getActorRole() {
        return actorRole;
    }

    public void setActorRole(Role actorRole) {
        this.actorRole = actorRole;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public EventStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(EventStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public EventStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(EventStatus newStatus) {
        this.newStatus = newStatus;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
