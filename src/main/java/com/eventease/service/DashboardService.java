package com.eventease.service;

import com.eventease.dto.DashboardStatsDto;
import com.eventease.entity.EventProposal;
import com.eventease.entity.EventStatus;
import com.eventease.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EventProposalRepository eventProposalRepository;
    private final EventRegistrationRepository registrationRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    public DashboardService(EventProposalRepository eventProposalRepository,
                            EventRegistrationRepository registrationRepository,
                            VenueRepository venueRepository,
                            UserRepository userRepository) {
        this.eventProposalRepository = eventProposalRepository;
        this.registrationRepository = registrationRepository;
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        List<EventProposal> allEvents = eventProposalRepository.findAll();

        stats.setTotalEvents((long) allEvents.size());
        stats.setPendingApprovals(allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.PENDING_HOD || 
                             e.getStatus() == EventStatus.PENDING_DEAN || 
                             e.getStatus() == EventStatus.PENDING_FINANCE)
                .count());
        stats.setApprovedEvents(allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.APPROVED)
                .count());
        stats.setRejectedEvents(allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.REJECTED)
                .count());
        stats.setChangesRequestedEvents(allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.CHANGES_REQUESTED)
                .count());
        stats.setPublishedEvents(allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .count());

        stats.setTotalRegistrations(registrationRepository.count());
        stats.setTotalVenues(venueRepository.count());
        stats.setTotalUsers(userRepository.count());

        Map<String, Long> byCategory = allEvents.stream()
                .filter(e -> e.getCategory() != null)
                .collect(Collectors.groupingBy(EventProposal::getCategory, Collectors.counting()));
        stats.setEventsByCategory(byCategory);

        Map<String, Long> byDepartment = allEvents.stream()
                .filter(e -> e.getDepartment() != null)
                .collect(Collectors.groupingBy(e -> e.getDepartment().getName(), Collectors.counting()));
        stats.setEventsByDepartment(byDepartment);

        return stats;
    }
}
