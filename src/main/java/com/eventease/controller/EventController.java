package com.eventease.controller;

import com.eventease.dto.ApprovalActionDto;
import com.eventease.dto.EventCreateRequest;
import com.eventease.dto.EventResponseDto;
import com.eventease.dto.EventUpdateRequest;
import com.eventease.entity.User;
import com.eventease.service.ApprovalService;
import com.eventease.service.EventService;
import com.eventease.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final ApprovalService approvalService;

    public EventController(EventService eventService, UserService userService, ApprovalService approvalService) {
        this.eventService = eventService;
        this.userService = userService;
        this.approvalService = approvalService;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventCreateRequest request) {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            EventResponseDto response = eventService.createEvent(request, currentUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listEvents() {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            List<EventResponseDto> list = eventService.listEventsForUser(currentUser);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            EventResponseDto event = eventService.getEventById(id, currentUser);
            return ResponseEntity.ok(event);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventUpdateRequest request) {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            EventResponseDto response = eventService.updateEvent(id, request, currentUser);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishEvent(@PathVariable Long id) {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            EventResponseDto published = eventService.publishEvent(id, currentUser);
            return ResponseEntity.ok(published);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<?> getEventHistory(@PathVariable Long id) {
        try {
            List<ApprovalActionDto> history = approvalService.getApprovalHistory(id);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/published")
    public ResponseEntity<?> getPublishedEvents() {
        try {
            List<EventResponseDto> published = eventService.getPublishedEvents();
            return ResponseEntity.ok(published);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
