package com.eventease.service;

import com.eventease.dto.AiCompletenessResponse;
import com.eventease.dto.AiDraftRequest;
import com.eventease.dto.AiDraftResponse;
import com.eventease.dto.AiSummaryResponse;
import com.eventease.entity.EventProposal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiService {

    public AiDraftResponse generateDraft(AiDraftRequest request) {
        String topic = request.getTopic() != null ? request.getTopic() : "Campus Technical Symposium";
        String category = request.getCategory() != null ? request.getCategory() : "Technical";
        String audience = request.getTargetAudience() != null ? request.getTargetAudience() : "Engineering Students & Faculty";
        String duration = request.getDuration() != null ? request.getDuration() : "Full Day (9:00 AM - 5:00 PM)";

        AiDraftResponse response = new AiDraftResponse();
        response.setTitle(topic + ": Innovation & Hands-On Practice Summit");
        response.setDescription("A comprehensive " + category.toLowerCase() + " event centered on " + topic + 
                ". The event brings together students, industry practitioners, and academic mentors for interactive workshops, " +
                "project exhibits, and keynote presentations designed to foster practical knowledge and collaborative problem solving.");

        response.setObjectives(
                "1. Introduce modern concepts and emerging trends in " + topic + ".\n" +
                "2. Provide participants with hands-on experiential learning and live laboratory sessions.\n" +
                "3. Enable peer-to-peer networking, knowledge exchange, and interdisciplinary collaboration.\n" +
                "4. Assess and award outstanding student project demonstrations and challenge solutions."
        );

        response.setSuggestedParticipants(120);
        response.setEstimatedBudget(new BigDecimal("15000.00"));
        response.setSuggestedBudgetBreakdown(
                "- Guest Speaker Honorarium & Travel: ₹6,000\n" +
                "- Participant Refreshments & Lunch: ₹5,000\n" +
                "- Workshop Kits & Printed Materials: ₹2,500\n" +
                "- Certificates, Badges & Trophies: ₹1,500"
        );

        response.setRecommendedChecklist(List.of(
                "Verify auditorium audio/video and projector equipment 48 hours prior",
                "Ensure faculty coordinator endorsement before department sign-off",
                "Prepare digital participant registration form and attendance QR scanner",
                "Arrange emergency first-aid kit and security notification"
        ));

        return response;
    }

    public AiCompletenessResponse evaluateCompleteness(String title, String description, String objectives,
                                                        String venue, BigDecimal budget, String budgetBreakdown,
                                                        Integer participants) {
        AiCompletenessResponse response = new AiCompletenessResponse();
        List<String> passed = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> recs = new ArrayList<>();
        int score = 100;

        if (title != null && title.trim().length() >= 8) {
            passed.add("Descriptive event title provided (" + title.trim().length() + " chars)");
        } else {
            score -= 15;
            missing.add("Event title is too brief or unspecified");
            recs.add("Use a specific, descriptive title indicating the theme or topic");
        }

        if (description != null && description.trim().length() >= 50) {
            passed.add("Detailed event description provided (" + description.trim().length() + " chars)");
        } else {
            score -= 20;
            missing.add("Description lacks sufficient depth (under 50 characters)");
            recs.add("Detail the agenda, intended impact, schedule structure, and deliverables");
        }

        if (objectives != null && objectives.trim().length() >= 20) {
            passed.add("Clear learning/cultural objectives specified");
        } else {
            score -= 15;
            missing.add("Measurable objectives not clearly articulated");
            recs.add("List 3 to 4 concrete outcomes attendees will gain from the session");
        }

        if (venue != null && !venue.trim().isEmpty()) {
            passed.add("Specific campus venue designated (" + venue + ")");
        } else {
            score -= 20;
            missing.add("No venue selected");
            recs.add("Select an available campus hall or auditorium");
        }

        if (budget != null && budget.compareTo(BigDecimal.ZERO) > 0) {
            if (budgetBreakdown != null && budgetBreakdown.trim().length() >= 15) {
                passed.add("Itemized budget breakdown included (₹" + budget + ")");
            } else {
                score -= 15;
                missing.add("Budget specified but lacks line-item breakdown");
                recs.add("Enumerate speaker fees, hospitality, logistics, and supplies");
            }
        } else {
            passed.add("Zero-cost / self-funded proposal noted");
        }

        if (participants != null && participants > 0) {
            passed.add("Participant capacity estimated (" + participants + " pax)");
        } else {
            score -= 10;
            missing.add("Target attendee count not specified");
            recs.add("Provide a realistic participant estimate to align with venue limits");
        }

        score = Math.max(0, Math.min(100, score));
        response.setScore(score);
        response.setPassedChecks(passed);
        response.setMissingItems(missing);
        response.setRecommendations(recs);

        if (score >= 90) {
            response.setStatus("Excellent - Ready for immediate submission");
        } else if (score >= 70) {
            response.setStatus("Good - Minor improvements recommended");
        } else if (score >= 50) {
            response.setStatus("Incomplete - Several key items require attention");
        } else {
            response.setStatus("Draft Quality - Critical proposal elements missing");
        }

        return response;
    }

    public AiSummaryResponse generateExecutiveSummary(EventProposal event) {
        AiSummaryResponse summary = new AiSummaryResponse();

        String deptName = event.getDepartment() != null ? event.getDepartment().getName() : "General";
        String venueName = event.getVenue() != null ? event.getVenue().getName() : "Unassigned Venue";
        int pax = event.getEstimatedParticipants() != null ? event.getEstimatedParticipants() : 0;
        BigDecimal budget = event.getBudget() != null ? event.getBudget() : BigDecimal.ZERO;

        summary.setExecutiveSummary(
                "Proposal '" + event.getTitle() + "' submitted by " +
                (event.getOrganizer() != null ? event.getOrganizer().getFullName() : "Organizer") +
                " (" + deptName + "). The initiative aims to conduct a structured " + event.getCategory() +
                " program focusing on knowledge enrichment and student participation."
        );

        summary.setAudienceAndScale(
                "Target Capacity: ~" + pax + " participants. " +
                "The target audience fits the selected venue (" + venueName + ") capacity well."
        );

        summary.setVenueAndLogisticsImpact(
                "Reserved Venue: " + venueName + ".\n" +
                "Timeframe: " + event.getStartDate() + " to " + event.getEndDate() + ".\n" +
                "Logistical overhead is moderate. Facilities like projector, audio, and seating must be verified."
        );

        summary.setBudgetAssessment(
                "Total Requested Budget: ₹" + budget + ".\n" +
                (budget.compareTo(new BigDecimal("10000")) > 0 
                    ? "Higher-tier expenditure flagged: Requires verified Dean & Finance oversight."
                    : "Standard operational budget within departmental discretion.")
        );

        List<String> risks = new ArrayList<>();
        if (pax > 150) {
            risks.add("High participant footfall: Requires coordinated security and crowd management.");
        }
        if (budget.compareTo(new BigDecimal("25000")) > 0) {
            risks.add("Substantial financial commitment: Invoices and receipts must be audited post-event.");
        }
        risks.add("Potential overlap with departmental examination or lecture timetables.");
        summary.setKeyRiskFactors(risks);

        summary.setRecommendedConditions(List.of(
                "Ensure faculty coordinator is physically present during the event",
                "Ensure venue is vacated and restored to clean order within 1 hour post-event",
                "Submit participant attendance roster to HOD office within 48 hours"
        ));

        return summary;
    }
}
