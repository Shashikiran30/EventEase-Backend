package com.eventease.controller;

import com.eventease.dto.EventResponseDto;
import com.eventease.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final EventService eventService;

    public CalendarController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<?> getCalendarEvents() {
        try {
            List<EventResponseDto> published = eventService.getPublishedEvents();
            return ResponseEntity.ok(published);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
