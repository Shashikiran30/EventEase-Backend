package com.eventease.controller;

import com.eventease.dto.AiCompletenessResponse;
import com.eventease.dto.AiDraftRequest;
import com.eventease.dto.AiDraftResponse;
import com.eventease.dto.AiSummaryResponse;
import com.eventease.entity.EventProposal;
import com.eventease.repository.EventProposalRepository;
import com.eventease.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final EventProposalRepository eventProposalRepository;

    public AiController(AiService aiService, EventProposalRepository eventProposalRepository) {
        this.aiService = aiService;
        this.eventProposalRepository = eventProposalRepository;
    }

    @PostMapping("/draft-proposal")
    public ResponseEntity<AiDraftResponse> draftProposal(@RequestBody AiDraftRequest request) {
        AiDraftResponse response = aiService.generateDraft(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-completeness")
    public ResponseEntity<AiCompletenessResponse> checkCompleteness(@RequestBody Map<String, Object> payload) {
        String title = (String) payload.get("title");
        String description = (String) payload.get("description");
        String objectives = (String) payload.get("objectives");
        String venue = (String) payload.get("venue");
        String budgetStr = payload.get("budget") != null ? payload.get("budget").toString() : "0";
        BigDecimal budget;
        try {
            budget = new BigDecimal(budgetStr);
        } catch (Exception e) {
            budget = BigDecimal.ZERO;
        }
        String budgetBreakdown = (String) payload.get("budgetBreakdown");
        Integer participants = payload.get("participants") != null ? 
                Integer.valueOf(payload.get("participants").toString()) : 0;

        AiCompletenessResponse response = aiService.evaluateCompleteness(
                title, description, objectives, venue, budget, budgetBreakdown, participants);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/executive-summary/{eventId}")
    public ResponseEntity<?> getExecutiveSummary(@PathVariable Long eventId) {
        return eventProposalRepository.findById(eventId)
                .map(event -> {
                    AiSummaryResponse summary = aiService.generateExecutiveSummary(event);
                    return ResponseEntity.ok(summary);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
