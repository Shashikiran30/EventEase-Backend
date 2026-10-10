package com.eventease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "approval_actions")
public class ApprovalAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private EventProposal eventProposal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_role", nullable = false, length = 50)
    private Role actorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private ActionType action; // SUBMIT, APPROVE, REJECT, REQUEST_CHANGES, RESUBMIT, PUBLISH

    @Column(length = 1000)
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 50)
    private EventStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 50)
    private EventStatus newStatus;

    private Integer stepOrder;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApprovalAction() {}

    public ApprovalAction(EventProposal eventProposal, User actor, Role actorRole, ActionType action,
                          String comments, EventStatus previousStatus, EventStatus newStatus, Integer stepOrder) {
        this.eventProposal = eventProposal;
        this.actor = actor;
        this.actorRole = actorRole;
        this.action = action;
        this.comments = comments;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.stepOrder = stepOrder;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventProposal getEventProposal() {
        return eventProposal;
    }

    public void setEventProposal(EventProposal eventProposal) {
        this.eventProposal = eventProposal;
    }

    public User getActor() {
        return actor;
    }

    public void setActor(User actor) {
        this.actor = actor;
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
