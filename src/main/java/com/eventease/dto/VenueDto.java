package com.eventease.dto;

import com.eventease.entity.Venue;
import java.time.LocalDateTime;

public class VenueDto {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private String facilities;
    private Boolean active;

    public VenueDto() {}

    public static VenueDto fromEntity(Venue venue) {
        if (venue == null) return null;
        VenueDto dto = new VenueDto();
        dto.setId(venue.getId());
        dto.setName(venue.getName());
        dto.setLocation(venue.getLocation());
        dto.setCapacity(venue.getCapacity());
        dto.setFacilities(venue.getFacilities());
        dto.setActive(venue.getActive());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String location() {
        return location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
