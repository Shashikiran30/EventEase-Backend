package com.eventease.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "approval_steps")
public class ApprovalStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    @JsonIgnore
    private ApprovalWorkflow workflow;

    @Column(nullable = false)
    private Integer stepOrder; // 1, 2, 3...

    @Column(nullable = false)
    private String stepName; // e.g. "HOD Endorsement", "Dean Final Review", "Finance Audit"

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "required_role", nullable = false, length = 50)
    private Role requiredRole; // ROLE_HOD, ROLE_DEAN, ROLE_FINANCE_OFFICER, ROLE_PRINCIPAL

    private Boolean isMandatory = true;

    public ApprovalStep() {}

    public ApprovalStep(ApprovalWorkflow workflow, Integer stepOrder, String stepName, Role requiredRole, Boolean isMandatory) {
        this.workflow = workflow;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.requiredRole = requiredRole;
        this.isMandatory = isMandatory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApprovalWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(ApprovalWorkflow workflow) {
        this.workflow = workflow;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Role getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(Role requiredRole) {
        this.requiredRole = requiredRole;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
}
