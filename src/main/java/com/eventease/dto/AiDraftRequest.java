package com.eventease.dto;

import java.util.List;

public class AiDraftRequest {
    private String topic;
    private String category;
    private String targetAudience;
    private String duration; // e.g. "Full Day", "2 Days", "3 Hours"
    private String primaryObjective;

    public AiDraftRequest() {}

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPrimaryObjective() {
        return primaryObjective;
    }

    public void setPrimaryObjective(String primaryObjective) {
        this.primaryObjective = primaryObjective;
    }
}
