package com.eventease.dto;

import com.eventease.entity.ActionType;
import jakarta.validation.constraints.NotNull;

public class ApprovalDecisionRequest {

    @NotNull(message = "Action type is required (APPROVE, REJECT, REQUEST_CHANGES)")
    private ActionType action;

    private String comments;

    public ApprovalDecisionRequest() {}

    public ApprovalDecisionRequest(ActionType action, String comments) {
        this.action = action;
        this.comments = comments;
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
}
