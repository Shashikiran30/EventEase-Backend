package com.eventease.dto;

import java.math.BigDecimal;
import java.util.List;

public class AiSummaryResponse {
    private String executiveSummary;
    private String audienceAndScale;
    private String venueAndLogisticsImpact;
    private String budgetAssessment;
    private List<String> keyRiskFactors;
    private List<String> recommendedConditions;

    public AiSummaryResponse() {}

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public void setExecutiveSummary(String executiveSummary) {
        this.executiveSummary = executiveSummary;
    }

    public String getAudienceAndScale() {
        return audienceAndScale;
    }

    public void setAudienceAndScale(String audienceAndScale) {
        this.audienceAndScale = audienceAndScale;
    }

    public String getVenueAndLogisticsImpact() {
        return venueAndLogisticsImpact;
    }

    public void setVenueAndLogisticsImpact(String venueAndLogisticsImpact) {
        this.venueAndLogisticsImpact = venueAndLogisticsImpact;
    }

    public String getBudgetAssessment() {
        return budgetAssessment;
    }

    public void setBudgetAssessment(String budgetAssessment) {
        this.budgetAssessment = budgetAssessment;
    }

    public List<String> getKeyRiskFactors() {
        return keyRiskFactors;
    }

    public void setKeyRiskFactors(List<String> keyRiskFactors) {
        this.keyRiskFactors = keyRiskFactors;
    }

    public List<String> getRecommendedConditions() {
        return recommendedConditions;
    }

    public void setRecommendedConditions(List<String> recommendedConditions) {
        this.recommendedConditions = recommendedConditions;
    }
}
