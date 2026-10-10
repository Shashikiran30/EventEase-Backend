package com.eventease.controller;

import com.eventease.dto.ApprovalDecisionRequest;
import com.eventease.dto.EventResponseDto;
import com.eventease.entity.User;
import com.eventease.service.ApprovalService;
import com.eventease.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;
    private final UserService userService;

    public ApprovalController(ApprovalService approvalService, UserService userService) {
        this.approvalService = approvalService;
        this.userService = userService;
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingApprovals() {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            if (currentUser.getRole() == com.eventease.entity.Role.ROLE_STUDENT) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied: Students cannot access authority approval queues."));
            }
            List<EventResponseDto> pending = approvalService.getPendingApprovalsForUser(currentUser);
            return ResponseEntity.ok(pending);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{eventId}/decision")
    public ResponseEntity<?> recordDecision(@PathVariable Long eventId,
                                           @Valid @RequestBody ApprovalDecisionRequest decisionRequest) {
        try {
            User currentUser = userService.getCurrentAuthenticatedUser();
            if (currentUser.getRole() == com.eventease.entity.Role.ROLE_STUDENT) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied: Students cannot record approval decisions."));
            }
            EventResponseDto updated = approvalService.processDecision(eventId, decisionRequest, currentUser);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
