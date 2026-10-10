package com.eventease.service;

import com.eventease.dto.EventResponseDto;
import com.eventease.dto.VenueAvailabilityCheck;
import com.eventease.dto.VenueDto;
import com.eventease.entity.EventProposal;
import com.eventease.entity.Venue;
import com.eventease.repository.EventProposalRepository;
import com.eventease.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueService {

    private final VenueRepository venueRepository;
    private final EventProposalRepository eventProposalRepository;

    public VenueService(VenueRepository venueRepository, EventProposalRepository eventProposalRepository) {
        this.venueRepository = venueRepository;
        this.eventProposalRepository = eventProposalRepository;
    }

    public List<VenueDto> getAllVenues() {
        return venueRepository.findAll().stream()
                .map(VenueDto::fromEntity)
                .collect(Collectors.toList());
    }

    public VenueDto getVenueById(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venue not found: " + id));
        return VenueDto.fromEntity(venue);
    }

    public VenueAvailabilityCheck checkAvailability(Long venueId, LocalDateTime start, LocalDateTime end, Long excludeEventId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found: " + venueId));

        if (start == null || end == null) {
            return new VenueAvailabilityCheck(venueId, venue.getName(), start, end, true, null, List.of());
        }

        if (end.isBefore(start) || end.isEqual(start)) {
            return new VenueAvailabilityCheck(venueId, venue.getName(), start, end, false,
                    "End time must be after start time", List.of());
        }

        List<EventProposal> conflicts = eventProposalRepository.findConflictingEvents(venueId, start, end, excludeEventId);

        if (!conflicts.isEmpty()) {
            List<EventResponseDto> conflictDtos = conflicts.stream()
                    .map(EventResponseDto::fromEntity)
                    .collect(Collectors.toList());
            String conflictNames = conflicts.stream()
                    .map(c -> "'" + c.getTitle() + "' (" + c.getStartDate().toLocalTime() + " - " + c.getEndDate().toLocalTime() + ")")
                    .collect(Collectors.joining(", "));
            return new VenueAvailabilityCheck(venueId, venue.getName(), start, end, false,
                    "Venue is already reserved during this time for: " + conflictNames, conflictDtos);
        }

        return new VenueAvailabilityCheck(venueId, venue.getName(), start, end, true, "Venue is fully available", List.of());
    }

    @Transactional
    public VenueDto createVenue(VenueDto dto) {
        Venue venue = new Venue(dto.getName(), dto.getLocation(), dto.getCapacity(), dto.getFacilities());
        if (dto.getActive() != null) {
            venue.setActive(dto.getActive());
        }
        Venue saved = venueRepository.save(venue);
        return VenueDto.fromEntity(saved);
    }
}
