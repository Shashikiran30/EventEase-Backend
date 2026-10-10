package com.eventease;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.eventease.dto.ApprovalDecisionRequest;
import com.eventease.dto.EventCreateRequest;
import com.eventease.dto.EventResponseDto;
import com.eventease.dto.RegisterRequest;
import com.eventease.entity.ActionType;
import com.eventease.entity.EventProposal;
import com.eventease.entity.EventStatus;
import com.eventease.entity.Role;
import com.eventease.entity.User;
import com.eventease.repository.DepartmentRepository;
import com.eventease.repository.EventProposalRepository;
import com.eventease.repository.UserRepository;
import com.eventease.service.ApprovalService;
import com.eventease.service.AuthService;
import com.eventease.service.EventService;

@SpringBootTest
public class EventEaseWorkflowTests {

    @Autowired
    private EventService eventService;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private EventProposalRepository eventProposalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Public student registration must force STUDENT role and department")
    @Transactional
    public void testPublicRegistrationRequiresStudentRoleAndDepartment() {
        var department = departmentRepository.findByCode("AIML").orElseThrow();
        RegisterRequest request = new RegisterRequest();
        request.setUsername("student_regression" + System.currentTimeMillis());
        request.setPassword("Password123");
        request.setFullName("Regression Student");
        request.setEmail("regression" + System.currentTimeMillis() + "@college.edu");
        request.setRole(Role.ROLE_ADMIN);
        request.setDepartmentId(department.getId());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertTrue(exception.getMessage().contains("student"));
    }

    @Test
    @DisplayName("Seeded HOD and Dean records use the required department-aware names")
    @Transactional
    public void testSeededDepartmentProfilesAreCorrect() {
        User aimlHod = userRepository.findByUsername("hod_aiml").orElseThrow();
        User cseHod = userRepository.findByUsername("hod_cse").orElseThrow();
        User dean = userRepository.findByUsername("dean").orElseThrow();

        assertEquals("Dr. Mallikarjun Reddy", aimlHod.getFullName());
        assertEquals("Dr. Vachonidhi", cseHod.getFullName());
        assertEquals("Dr. Vijay Kumar", dean.getFullName());
        assertEquals("AIML", aimlHod.getDepartment().getCode());
        assertEquals("CSE", cseHod.getDepartment().getCode());
    }

    @Test
    @DisplayName("Checklist #1 & #5: Student can submit proposal with initial audit trail")
    @Transactional
    public void testStudentCanSubmitProposal() {
        User student = userRepository.findByUsername("student").orElseThrow();

        EventCreateRequest req = new EventCreateRequest();
        req.setTitle("Robotics AI Workshop 2026");
        req.setDescription("Autonomous navigation hands-on workshop for engineering undergraduates.");
        req.setObjectives("Learn ROS2, path planning, and sensor fusion.");
        req.setCategory("Technical");
        req.setDepartmentId(student.getDepartment().getId());
        req.setVenueId(2L); // Visvesvaraya Seminar Hall
        req.setStartDate(LocalDateTime.now().plusDays(50));
        req.setEndDate(LocalDateTime.now().plusDays(51));
        req.setEstimatedParticipants(80);
        req.setBudget(new BigDecimal("12000.00"));
        req.setSubmitImmediately(true);

        EventResponseDto created = eventService.createEvent(req, student);

        assertNotNull(created.getId());
        assertEquals(EventStatus.PENDING_HOD, created.getStatus());
        assertEquals(1, created.getCurrentStepOrder());
        assertFalse(created.getApprovalHistory().isEmpty());
        assertEquals(ActionType.SUBMIT, created.getApprovalHistory().get(0).getAction());
    }

    @Test
    @DisplayName("Checklist #3: Dean cannot act before HOD step is completed")
    @Transactional
    public void testDeanCannotActBeforeHodStep() {
        User student = userRepository.findByUsername("student").orElseThrow();
        User dean = userRepository.findByUsername("dean").orElseThrow();

        EventCreateRequest req = new EventCreateRequest();
        req.setTitle("Dean Order Verification Test Event");
        req.setDescription("Testing that Dean cannot leapfrog the HOD review step.");
        req.setCategory("Technical");
        req.setDepartmentId(student.getDepartment().getId());
        req.setVenueId(1L);
        req.setStartDate(LocalDateTime.now().plusDays(150));
        req.setEndDate(LocalDateTime.now().plusDays(151));
        req.setSubmitImmediately(true);

        EventResponseDto created = eventService.createEvent(req, student);
        assertEquals(EventStatus.PENDING_HOD, created.getStatus());

        // Attempt Dean approval while still in PENDING_HOD
        ApprovalDecisionRequest decision = new ApprovalDecisionRequest(ActionType.APPROVE, "Dean premature approval attempt");
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            approvalService.processDecision(created.getId(), decision, dean);
        });

        assertTrue(exception.getMessage().contains("Dean cannot review this proposal before mandatory HOD approval"));
    }

    @Test
    @DisplayName("Checklist #6: Request for changes returns proposal to organizer with history preserved")
    @Transactional
    public void testRequestChangesPreservesHistory() {
        // Find an event in PENDING_HOD
        EventProposal e1 = eventProposalRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.PENDING_HOD)
                .findFirst()
                .orElseThrow();

        // Find the specific HOD for this event's department
        User hod = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_HOD && u.getDepartment().getId().equals(e1.getDepartment().getId()))
                .findFirst()
                .orElseThrow();

        ApprovalDecisionRequest changesReq = new ApprovalDecisionRequest(
                ActionType.REQUEST_CHANGES, "Please attach detailed safety measures."
        );

        EventResponseDto updated = approvalService.processDecision(e1.getId(), changesReq, hod);

        assertEquals(EventStatus.CHANGES_REQUESTED, updated.getStatus());
        assertTrue(updated.getApprovalHistory().stream()
                .anyMatch(a -> a.getAction() == ActionType.REQUEST_CHANGES && a.getComments().contains("safety measures")));
    }

    @Test
    @DisplayName("Checklist #7: Incompletely approved events cannot be published")
    @Transactional
    public void testIncompleteEventCannotBePublished() {
        User student = userRepository.findByUsername("student").orElseThrow();

        // Event in PENDING_DEAN cannot be published
        EventProposal pendingDean = eventProposalRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.PENDING_DEAN)
                .findFirst()
                .orElseThrow();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            eventService.publishEvent(pendingDean.getId(), student);
        });

        assertTrue(exception.getMessage().contains("Only APPROVED proposals can be published"));
    }

    @Test
    @DisplayName("Checklist #8: Venue reservations cannot overlap")
    @Transactional
    public void testVenueConflictPrevention() {
        User student = userRepository.findByUsername("student").orElseThrow();

        // Event 5 is PUBLISHED at seminarHall (Venue 2)
        EventProposal published = eventProposalRepository.findAll().stream()
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .findFirst()
                .orElseThrow();

        EventCreateRequest conflicting = new EventCreateRequest();
        conflicting.setTitle("Conflicting Workshop at same time and venue");
        conflicting.setDescription("Attempting double booking of same venue.");
        conflicting.setCategory("Workshop");
        conflicting.setDepartmentId(student.getDepartment().getId());
        conflicting.setVenueId(published.getVenue().getId());
        // Exact same time range
        conflicting.setStartDate(published.getStartDate());
        conflicting.setEndDate(published.getEndDate());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(conflicting, student);
        });

        assertTrue(exception.getMessage().contains("Venue is already reserved during this time"));
    }

    @Test
    @DisplayName("Checklist #9: User can authenticate via username or email")
    @Transactional
    public void testUserAuthenticationWithUsernameAndEmail() {
        // Authenticate via username
        var loginReqUsername = new com.eventease.dto.LoginRequest("student", "password123");
        var res1 = authService.authenticate(loginReqUsername);
        assertNotNull(res1.getToken());
        assertEquals("student", res1.getUser().getUsername());

        // Authenticate via email
        var loginReqEmail = new com.eventease.dto.LoginRequest("shashi.kiran@college.edu", "password123");
        var res2 = authService.authenticate(loginReqEmail);
        assertNotNull(res2.getToken());
        assertEquals("student", res2.getUser().getUsername());

        // Invalid credentials throws BadCredentialsException
        var loginReqInvalid = new com.eventease.dto.LoginRequest("student", "wrong_password");
        assertThrows(org.springframework.security.authentication.BadCredentialsException.class, () -> {
            authService.authenticate(loginReqInvalid);
        });
    }

    @Test
    @DisplayName("Role enum and approval workflow support ROLE_PRINCIPAL and PENDING_PRINCIPAL")
    @Transactional
    public void testRoleEnumContainsPrincipal() {
        assertNotNull(Role.valueOf("ROLE_PRINCIPAL"));
        assertNotNull(EventStatus.valueOf("PENDING_PRINCIPAL"));
    }

    @Test
    @DisplayName("HOD cannot approve event proposals from other departments")
    @Transactional
    public void testHodCannotApproveOtherDepartmentProposal() {
        User aimlHod = userRepository.findByUsername("hod_aiml").orElseThrow();
        var cseDept = departmentRepository.findByCode("CSE").orElseThrow();
        User studentCse = userRepository.findByUsername("student_cse").orElseThrow();

        EventCreateRequest req = new EventCreateRequest();
        req.setTitle("CSE Specific Hackathon");
        req.setDescription("Testing HOD cross-department barrier.");
        req.setCategory("Technical");
        req.setDepartmentId(cseDept.getId());
        req.setVenueId(1L);
        req.setStartDate(LocalDateTime.now().plusDays(200));
        req.setEndDate(LocalDateTime.now().plusDays(201));
        req.setSubmitImmediately(true);

        EventResponseDto created = eventService.createEvent(req, studentCse);
        assertEquals(EventStatus.PENDING_HOD, created.getStatus());

        ApprovalDecisionRequest decision = new ApprovalDecisionRequest(ActionType.APPROVE, "Cross-department approval attempt");
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            approvalService.processDecision(created.getId(), decision, aimlHod);
        });
        assertTrue(ex.getMessage().contains("You can only review event proposals for your own department"));
    }

    @Test
    @DisplayName("Pending Principal status allows Principal review and rejects unauthorized roles")
    @Transactional
    public void testPrincipalAuthorizationOnPendingPrincipal() {
        User dean = userRepository.findByUsername("dean").orElseThrow();
        User student = userRepository.findByUsername("student").orElseThrow();

        EventProposal proposal = new EventProposal();
        proposal.setTitle("Institutional Research Fest");
        proposal.setDescription("Testing Principal approval stage");
        proposal.setCategory("Technical");
        proposal.setDepartment(student.getDepartment());
        proposal.setOrganizer(student);
        proposal.setVenue(eventProposalRepository.findAll().get(0).getVenue());
        proposal.setStartDate(LocalDateTime.now().plusDays(100));
        proposal.setEndDate(LocalDateTime.now().plusDays(101));
        proposal.setStatus(EventStatus.PENDING_PRINCIPAL);
        proposal.setCurrentStepOrder(3);
        EventProposal saved = eventProposalRepository.save(proposal);

        ApprovalDecisionRequest decision = new ApprovalDecisionRequest(ActionType.APPROVE, "Dean premature signoff");
        SecurityException ex = assertThrows(SecurityException.class, () -> {
            approvalService.processDecision(saved.getId(), decision, dean);
        });
        assertTrue(ex.getMessage().contains("Only the Principal or Administrator can review at this stage"));

        User principal = new User("principal_test", "pass", "Dr. Principal", "principal.test@college.edu",
                Role.ROLE_PRINCIPAL, null, "Principal");
        principal = userRepository.save(principal);

        ApprovalDecisionRequest principalDecision = new ApprovalDecisionRequest(ActionType.APPROVE, "Final institutional sanction granted");
        EventResponseDto approved = approvalService.processDecision(saved.getId(), principalDecision, principal);
        assertEquals(EventStatus.APPROVED, approved.getStatus());
    }
}
