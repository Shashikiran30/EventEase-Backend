package com.eventease.dto;

import java.util.List;

public class AiCompletenessResponse {
    private Integer score; // 0 to 100
    private String status; // Excellent, Good, Incomplete, Critical Missing Info
    private List<String> passedChecks;
    private List<String> missingItems;
    private List<String> recommendations;

    public AiCompletenessResponse() {}

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPassedChecks() {
        return passedChecks;
    }

    public void setPassedChecks(List<String> passedChecks) {
        this.passedChecks = passedChecks;
    }

    public List<String> getMissingItems() {
        return missingItems;
    }

    public void setMissingItems(List<String> missingItems) {
        this.missingItems = missingItems;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
