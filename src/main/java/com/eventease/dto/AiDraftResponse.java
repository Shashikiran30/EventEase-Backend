package com.eventease.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiDraftResponse {
    private String title;
    private String description;
    private String objectives;
    private Integer suggestedParticipants;
    private BigDecimal estimatedBudget;
    private String suggestedBudgetBreakdown;
    private List<String> recommendedChecklist;

    public AiDraftResponse() {}

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

    public Integer getSuggestedParticipants() {
        return suggestedParticipants;
    }

    public void setSuggestedParticipants(Integer suggestedParticipants) {
        this.suggestedParticipants = suggestedParticipants;
    }

    public BigDecimal getEstimatedBudget() {
        return estimatedBudget;
    }

    public void setEstimatedBudget(BigDecimal estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }

    public String getSuggestedBudgetBreakdown() {
        return suggestedBudgetBreakdown;
    }

    public void setSuggestedBudgetBreakdown(String suggestedBudgetBreakdown) {
        this.suggestedBudgetBreakdown = suggestedBudgetBreakdown;
    }

    public List<String> getRecommendedChecklist() {
        return recommendedChecklist;
    }

    public void setRecommendedChecklist(List<String> recommendedChecklist) {
        this.recommendedChecklist = recommendedChecklist;
    }
}
