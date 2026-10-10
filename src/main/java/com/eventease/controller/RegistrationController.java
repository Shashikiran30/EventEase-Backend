package com.eventease.controller;

import com.eventease.dto.RegistrationRequest;
import com.eventease.dto.RegistrationResponseDto;
import com.eventease.entity.User;
import com.eventease.service.RegistrationService;
import com.eventease.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserService userService;

    public RegistrationController(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @PostMapping("/events/{id}/registrations")
    public ResponseEntity<?> registerForEvent(@PathVariable Long id, @RequestBody(required = false) RegistrationRequest request) {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            RegistrationRequest req = request != null ? request : new RegistrationRequest();
            RegistrationResponseDto response = registrationService.registerForEvent(id, req, currentUser);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/registrations/my")
    public ResponseEntity<?> getMyRegistrations() {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            List<RegistrationResponseDto> list = registrationService.getRegistrationsForUser(currentUser.getId());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/events/{id}/registrations")
    public ResponseEntity<?> getEventRegistrations(@PathVariable Long id) {
        try {
            List<RegistrationResponseDto> list = registrationService.getRegistrationsForEvent(id);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
