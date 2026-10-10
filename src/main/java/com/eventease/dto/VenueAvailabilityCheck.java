package com.eventease.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VenueAvailabilityCheck {
    private Long venueId;
    private String venueName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isAvailable;
    private String conflictReason;
    private List<EventResponseDto> conflictingEvents;

    public VenueAvailabilityCheck() {}

    public VenueAvailabilityCheck(Long venueId, String venueName, LocalDateTime startDate, LocalDateTime endDate,
                                  Boolean isAvailable, String conflictReason, List<EventResponseDto> conflictingEvents) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAvailable = isAvailable;
        this.conflictReason = conflictReason;
        this.conflictingEvents = conflictingEvents;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getConflictReason() {
        return conflictReason;
    }

    public void setConflictReason(String conflictReason) {
        this.conflictReason = conflictReason;
    }

    public List<EventResponseDto> getConflictingEvents() {
        return conflictingEvents;
    }

    public void setConflictingEvents(List<EventResponseDto> conflictingEvents) {
        this.conflictingEvents = conflictingEvents;
    }
}
