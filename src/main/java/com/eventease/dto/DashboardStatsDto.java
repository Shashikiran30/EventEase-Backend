package com.eventease.dto;

import java.util.Map;

public class DashboardStatsDto {
    private Long totalEvents;
    private Long pendingApprovals;
    private Long approvedEvents;
    private Long rejectedEvents;
    private Long changesRequestedEvents;
    private Long publishedEvents;
    private Long totalRegistrations;
    private Long totalVenues;
    private Long totalUsers;
    private Map<String, Long> eventsByCategory;
    private Map<String, Long> eventsByDepartment;

    public DashboardStatsDto() {}

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(Long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public Long getApprovedEvents() {
        return approvedEvents;
    }

    public void setApprovedEvents(Long approvedEvents) {
        this.approvedEvents = approvedEvents;
    }

    public Long getRejectedEvents() {
        return rejectedEvents;
    }

    public void setRejectedEvents(Long rejectedEvents) {
        this.rejectedEvents = rejectedEvents;
    }

    public Long getChangesRequestedEvents() {
        return changesRequestedEvents;
    }

    public void setChangesRequestedEvents(Long changesRequestedEvents) {
        this.changesRequestedEvents = changesRequestedEvents;
    }

    public Long getPublishedEvents() {
        return publishedEvents;
    }

    public void setPublishedEvents(Long publishedEvents) {
        this.publishedEvents = publishedEvents;
    }

    public Long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(Long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public Long getTotalVenues() {
        return totalVenues;
    }

    public void setTotalVenues(Long totalVenues) {
        this.totalVenues = totalVenues;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Map<String, Long> getEventsByCategory() {
        return eventsByCategory;
    }

    public void setEventsByCategory(Map<String, Long> eventsByCategory) {
        this.eventsByCategory = eventsByCategory;
    }

    public Map<String, Long> getEventsByDepartment() {
        return eventsByDepartment;
    }

    public void setEventsByDepartment(Map<String, Long> eventsByDepartment) {
        this.eventsByDepartment = eventsByDepartment;
    }
}
