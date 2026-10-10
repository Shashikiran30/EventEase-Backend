package com.eventease.service;

import com.eventease.dto.RegistrationRequest;
import com.eventease.dto.RegistrationResponseDto;
import com.eventease.entity.EventProposal;
import com.eventease.entity.EventRegistration;
import com.eventease.entity.EventStatus;
import com.eventease.entity.User;
import com.eventease.repository.EventProposalRepository;
import com.eventease.repository.EventRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventProposalRepository eventProposalRepository;
    private final NotificationService notificationService;

    public RegistrationService(EventRegistrationRepository registrationRepository,
                               EventProposalRepository eventProposalRepository,
                               NotificationService notificationService) {
        this.registrationRepository = registrationRepository;
        this.eventProposalRepository = eventProposalRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public RegistrationResponseDto registerForEvent(Long eventId, RegistrationRequest request, User user) {
        EventProposal event = eventProposalRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new IllegalStateException("Registrations are only open for PUBLISHED events.");
        }

        if (registrationRepository.existsByEventIdAndUserId(eventId, user.getId())) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        Long currentCount = registrationRepository.countByEventId(eventId);
        if (event.getVenue() != null && event.getVenue().getCapacity() != null) {
            if (currentCount >= event.getVenue().getCapacity()) {
                throw new IllegalStateException("Event registration is full! Capacity of " + event.getVenue().getCapacity() + " reached.");
            }
        }

        String studentName = request.getStudentName() != null ? request.getStudentName() : user.getFullName();
        String studentEmail = request.getStudentEmail() != null ? request.getStudentEmail() : user.getEmail();
        String studentRoll = request.getStudentRollNo() != null ? request.getStudentRollNo() : "STU-" + user.getId();
        String dept = request.getStudentDepartment() != null ? request.getStudentDepartment() : 
                (user.getDepartment() != null ? user.getDepartment().getName() : "General");

        String ticketCode = "TKT-" + event.getId() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        EventRegistration registration = new EventRegistration(
                event, user, studentName, studentEmail, studentRoll, dept, ticketCode
        );

        EventRegistration saved = registrationRepository.save(registration);

        notificationService.sendNotification(
                user,
                "Registration Confirmed: " + event.getTitle(),
                "You have successfully registered for '" + event.getTitle() + "'. Your Ticket Pass ID: " + ticketCode,
                "REGISTRATION",
                event.getId()
        );

        return RegistrationResponseDto.fromEntity(saved);
    }

    public List<RegistrationResponseDto> getRegistrationsForUser(Long userId) {
        return registrationRepository.findByUserIdOrderByRegisteredAtDesc(userId).stream()
                .map(RegistrationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<RegistrationResponseDto> getRegistrationsForEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .map(RegistrationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
