package com.eventease.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eventease.entity.ActionType;
import com.eventease.entity.ApprovalAction;
import com.eventease.entity.ApprovalStep;
import com.eventease.entity.ApprovalWorkflow;
import com.eventease.entity.Department;
import com.eventease.entity.EventProposal;
import com.eventease.entity.EventRegistration;
import com.eventease.entity.EventStatus;
import com.eventease.entity.Role;
import com.eventease.entity.User;
import com.eventease.entity.Venue;
import com.eventease.repository.ApprovalActionRepository;
import com.eventease.repository.ApprovalStepRepository;
import com.eventease.repository.ApprovalWorkflowRepository;
import com.eventease.repository.DepartmentRepository;
import com.eventease.repository.EventProposalRepository;
import com.eventease.repository.EventRegistrationRepository;
import com.eventease.repository.UserRepository;
import com.eventease.repository.VenueRepository;

@Component
public class DataLoader implements CommandLineRunner {

        private final UserRepository userRepository;
        private final DepartmentRepository departmentRepository;
        private final VenueRepository venueRepository;
        private final ApprovalWorkflowRepository workflowRepository;
        private final ApprovalStepRepository stepRepository;
        private final EventProposalRepository eventProposalRepository;
        private final ApprovalActionRepository approvalActionRepository;
        private final EventRegistrationRepository registrationRepository;
        private final PasswordEncoder passwordEncoder;

        public DataLoader(UserRepository userRepository,
                        DepartmentRepository departmentRepository,
                        VenueRepository venueRepository,
                        ApprovalWorkflowRepository workflowRepository,
                        ApprovalStepRepository stepRepository,
                        EventProposalRepository eventProposalRepository,
                        ApprovalActionRepository approvalActionRepository,
                        EventRegistrationRepository registrationRepository,
                        PasswordEncoder passwordEncoder) {
                this.userRepository = userRepository;
                this.departmentRepository = departmentRepository;
                this.venueRepository = venueRepository;
                this.workflowRepository = workflowRepository;
                this.stepRepository = stepRepository;
                this.eventProposalRepository = eventProposalRepository;
                this.approvalActionRepository = approvalActionRepository;
                this.registrationRepository = registrationRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                Department cse = departmentRepository.findByCode("CSE")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("CSE", "Computer Science & Engineering",
                                                                "Computing, Software & Systems")));
                Department ece = departmentRepository.findByCode("ECE")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("ECE", "Electronics & Communication",
                                                                "Hardware, VLSI & Communications")));
                Department mech = departmentRepository.findByCode("MECH")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("MECH", "Mechanical Engineering",
                                                                "Design, Robotics & Manufacturing")));
                Department mba = departmentRepository.findByCode("MBA")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("MBA", "School of Management",
                                                                "Business Strategy, Finance & Marketing")));
                Department aiml = departmentRepository.findByCode("AIML")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("AIML",
                                                                "Artificial Intelligence & Machine Learning",
                                                                "AI, ML, Deep Learning & Data Science")));
                Department ai = departmentRepository.findByCode("AI")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("AI", "Artificial Intelligence",
                                                                "Core AI, Neural Networks & Cognitive Computing")));
                Department civil = departmentRepository.findByCode("CIVIL")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("CIVIL", "Civil Engineering",
                                                                "Structural, Environmental & Geotechnical Engineering")));
                Department cs = departmentRepository.findByCode("CS")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("CS", "Computer Science",
                                                                "Core Computing, Algorithms & Systems")));
                Department ds = departmentRepository.findByCode("DS")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("DS", "Data Science",
                                                                "Statistics, Big Data & Analytics")));
                Department it = departmentRepository.findByCode("IT")
                                .orElseGet(() -> departmentRepository.save(
                                                new Department("IT", "Information Technology",
                                                                "Networks, Security & IT Systems")));

                if (venueRepository.count() == 0) {
                        venueRepository.save(new Venue(
                                        "Main Auditorium",
                                        "Central Campus Block A, Ground Floor",
                                        500,
                                        "Surround Sound, Dual 4K Projectors, Stage Lighting, Central AC"));
                        venueRepository.save(new Venue(
                                        "Visvesvaraya Seminar Hall",
                                        "Academic Block 2, 2nd Floor",
                                        150,
                                        "Smart Board, PTZ Camera, Podium Mic, High-speed Wi-Fi"));
                        venueRepository.save(new Venue(
                                        "Turing Computing Lab",
                                        "CS Building, 3rd Floor",
                                        80,
                                        "80 High-End Workstations, Dual Monitors, Gigabit LAN, Projector"));
                        venueRepository.save(new Venue(
                                        "Open Air Amphitheatre",
                                        "Campus Green Lawns",
                                        800,
                                        "Outdoor PA System, Tiered Seating, Stage Trussing, Ambient Floodlights"));
                        venueRepository.save(new Venue(
                                        "Executive Boardroom B",
                                        "Administrative Block, 1st Floor",
                                        40,
                                        "Video Conferencing Screen, Conference Mic Matrix, Ergonomic Chairs"));
                }

                ApprovalWorkflow standardWorkflow = workflowRepository.findByName("Standard 2-Level Academic Workflow")
                                .orElseGet(() -> {
                                        ApprovalWorkflow created = new ApprovalWorkflow(
                                                        "Standard 2-Level Academic Workflow",
                                                        "Mandatory sequential review: Head of Department followed by Dean of Academic Affairs",
                                                        "STANDARD");
                                        created = workflowRepository.save(created);
                                        ApprovalStep step1 = new ApprovalStep(created, 1, "Department HOD Endorsement",
                                                        Role.ROLE_HOD, true);
                                        ApprovalStep step2 = new ApprovalStep(created, 2, "Dean of Academic Affairs Approval",
                                                        Role.ROLE_DEAN, true);
                                        created.getSteps().add(step1);
                                        created.getSteps().add(step2);
                                        return workflowRepository.save(created);
                                });

                String encodedPass = passwordEncoder.encode("password123");
                User student = userRepository.findByUsername("student")
                                .orElseGet(() -> userRepository.save(new User(
                                                "student", encodedPass, "Shashi Kiran", "shashi.kiran@college.edu",
                                                Role.ROLE_STUDENT, aiml, "Student Council President")));
                User studentCse = userRepository.findByUsername("student_cse")
                                .orElseGet(() -> userRepository.save(new User(
                                                "student_cse", encodedPass, "Aravind Kumar", "aravind.cse@college.edu",
                                                Role.ROLE_STUDENT, cse, "CSE Student Representative")));
                User student2 = userRepository.findByUsername("organizer2")
                                .orElseGet(() -> userRepository.save(new User(
                                                "organizer2", encodedPass, "Priya Patel", "priya.patel@college.edu",
                                                Role.ROLE_STUDENT, ece, "Tech Club Lead")));
                User facultyCoord = userRepository.findByUsername("faculty")
                                .orElseGet(() -> userRepository.save(new User(
                                                "faculty", encodedPass, "Dr. Arvind Swamy", "arvind.swamy@college.edu",
                                                Role.ROLE_FACULTY_COORDINATOR, cse, "Associate Professor")));
                User hodCse = userRepository.findByUsername("hod_cse")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_cse", encodedPass, "Dr. Vachonidhi", "hod.cse@college.edu",
                                                Role.ROLE_HOD, cse, "Head of Department (CSE)")));
                User hodAiml = userRepository.findByUsername("hod_aiml")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_aiml", encodedPass, "Dr. Mallikarjun Reddy", "hod.aiml@college.edu",
                                                Role.ROLE_HOD, aiml, "Head of Department (AIML)")));
                User hodEc = userRepository.findByUsername("hod_ece")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_ece", encodedPass, "Dr. U.V Tanush", "hod.ece@college.edu",
                                                Role.ROLE_HOD, ece, "Head of Department (ECE)")));
                User hodMech = userRepository.findByUsername("hod_mech")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_mech", encodedPass, "Dr. Pradyumna", "hod.mech@college.edu",
                                                Role.ROLE_HOD, mech, "Head of Department (Mechanical)")));
                User hodAi = userRepository.findByUsername("hod_ai")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_ai", encodedPass, "Dr. Trupthi", "hod.ai@college.edu",
                                                Role.ROLE_HOD, ai, "Head of Department (AI)")));
                User hodCivil = userRepository.findByUsername("hod_civil")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_civil", encodedPass, "Dr. Bangaru Konda", "hod.civil@college.edu",
                                                Role.ROLE_HOD, civil, "Head of Department (Civil)")));
                User hodCs = userRepository.findByUsername("hod_cs")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_cs", encodedPass, "Dr. Prajwel Reddy", "hod.cs@college.edu",
                                                Role.ROLE_HOD, cs, "Head of Department (CS)")));
                User hodDs = userRepository.findByUsername("hod_ds")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_ds", encodedPass, "Dr. Md Abrar Ansar", "hod.ds@college.edu",
                                                Role.ROLE_HOD, ds, "Head of Department (DS)")));
                User hodIt = userRepository.findByUsername("hod_it")
                                .orElseGet(() -> userRepository.save(new User(
                                                "hod_it", encodedPass, "Dr. Shashank Reddy", "hod.it@college.edu",
                                                Role.ROLE_HOD, it, "Head of Department (IT)")));
                User dean = userRepository.findByUsername("dean")
                                .orElseGet(() -> userRepository.save(new User(
                                                "dean", encodedPass, "Dr. Vijay Kumar", "dean.soe@college.edu",
                                                Role.ROLE_DEAN, null, "Dean, School of Engineering (SOE)")));
                userRepository.findByUsername("finance")
                                .orElseGet(() -> userRepository.save(new User(
                                                "finance", encodedPass, "Suresh Nair", "finance.officer@college.edu",
                                                Role.ROLE_FINANCE_OFFICER, null, "Chief Accounts & Finance Officer")));
                userRepository.findByUsername("admin")
                                .orElseGet(() -> userRepository.save(new User(
                                                "admin", encodedPass, "System Administrator", "admin@college.edu",
                                                Role.ROLE_ADMIN, null, "Campus IT Systems Administrator")));

                Venue aud = venueRepository.findByName("Main Auditorium")
                                .orElseGet(() -> venueRepository.save(new Venue(
                                                "Main Auditorium",
                                                "Central Campus Block A, Ground Floor",
                                                500,
                                                "Surround Sound, Dual 4K Projectors, Stage Lighting, Central AC")));
                Venue seminarHall = venueRepository.findByName("Visvesvaraya Seminar Hall")
                                .orElseGet(() -> venueRepository.save(new Venue(
                                                "Visvesvaraya Seminar Hall",
                                                "Academic Block 2, 2nd Floor",
                                                150,
                                                "Smart Board, PTZ Camera, Podium Mic, High-speed Wi-Fi")));
                Venue labComplex = venueRepository.findByName("Turing Computing Lab")
                                .orElseGet(() -> venueRepository.save(new Venue(
                                                "Turing Computing Lab",
                                                "CS Building, 3rd Floor",
                                                80,
                                                "80 High-End Workstations, Dual Monitors, Gigabit LAN, Projector")));
                Venue amphitheatre = venueRepository.findByName("Open Air Amphitheatre")
                                .orElseGet(() -> venueRepository.save(new Venue(
                                                "Open Air Amphitheatre",
                                                "Campus Green Lawns",
                                                800,
                                                "Outdoor PA System, Tiered Seating, Stage Trussing, Ambient Floodlights")));
                Venue boardroom = venueRepository.findByName("Executive Boardroom B")
                                .orElseGet(() -> venueRepository.save(new Venue(
                                                "Executive Boardroom B",
                                                "Administrative Block, 1st Floor",
                                                40,
                                                "Video Conferencing Screen, Conference Mic Matrix, Ergonomic Chairs")));

                if (eventProposalRepository.count() == 0) {
                        EventProposal e1 = new EventProposal();
                        e1.setTitle("National Hackathon 2026: CodeSpark");
                        e1.setDescription("A 36-hour inter-college hackathon focusing on Generative AI, Web3, and Climate-Tech innovations. Includes mentorship by industry architects and venture pitch rounds.");
                        e1.setObjectives("1. Enhance rapid software prototyping skills.\n2. Encourage cross-disciplinary collaboration.\n3. Connect students with venture incubators.");
                        e1.setCategory("Technical");
                        e1.setDepartment(aiml);
                        e1.setOrganizer(student);
                        e1.setFacultyCoordinator(facultyCoord);
                        e1.setVenue(labComplex);
                        e1.setStartDate(LocalDateTime.now().plusDays(15).withHour(9).withMinute(0));
                        e1.setEndDate(LocalDateTime.now().plusDays(16).withHour(21).withMinute(0));
                        e1.setEstimatedParticipants(75);
                        e1.setBudget(new BigDecimal("18500.00"));
                        e1.setBudgetBreakdown("Snacks & Energy Drinks: ₹8,000; Badges & Swag: ₹4,500; Cash Prizes: ₹6,000");
                        e1.setStatus(EventStatus.PENDING_HOD);
                        e1.setCurrentStepOrder(1);
                        e1.setWorkflow(standardWorkflow);
                        e1.setAiCompletenessScore(92);
                        e1.setAiSummary("Executive Summary: 36-hour technical hackathon with ~75 participants. Logistical requirements verified with Turing Lab. Budget ₹18,500 is within standard annual tech fest allocation.");
                        e1 = eventProposalRepository.save(e1);

                        approvalActionRepository.save(new ApprovalAction(
                                        e1, student, Role.ROLE_STUDENT, ActionType.SUBMIT,
                                        "Initial hackathon proposal submitted for departmental sanction.",
                                        EventStatus.DRAFT, EventStatus.PENDING_HOD, 1));

                        EventProposal e2 = new EventProposal();
                        e2.setTitle("AI & Quantum Computing Summit");
                        e2.setDescription("An international conference featuring talks from leading researchers on hybrid quantum algorithms, tensor processing, and next-generation neural networks.");
                        e2.setObjectives("1. Introduce quantum compilation principles.\n2. Showcase research posters.\n3. Foster university-industry R&D partnerships.");
                        e2.setCategory("Technical");
                        e2.setDepartment(cse);
                        e2.setOrganizer(student);
                        e2.setFacultyCoordinator(facultyCoord);
                        e2.setVenue(aud);
                        e2.setStartDate(LocalDateTime.now().plusDays(25).withHour(10).withMinute(0));
                        e2.setEndDate(LocalDateTime.now().plusDays(25).withHour(17).withMinute(0));
                        e2.setEstimatedParticipants(350);
                        e2.setBudget(new BigDecimal("28000.00"));
                        e2.setBudgetBreakdown("Keynote Honorarium: ₹12,000; Delegate Kits: ₹8,000; High Tea: ₹8,000");
                        e2.setStatus(EventStatus.PENDING_DEAN);
                        e2.setCurrentStepOrder(2);
                        e2.setWorkflow(standardWorkflow);
                        e2.setAiCompletenessScore(95);
                        e2.setAiSummary("Executive Summary: High-profile research conference. 350 attendees in Main Auditorium. HOD has endorsed with strong recommendation. Dean review pending.");
                        e2 = eventProposalRepository.save(e2);

                        approvalActionRepository.save(new ApprovalAction(
                                        e2, student, Role.ROLE_STUDENT, ActionType.SUBMIT,
                                        "Proposal submitted with invited guest speaker bios.",
                                        EventStatus.DRAFT, EventStatus.PENDING_HOD, 1));
                        approvalActionRepository.save(new ApprovalAction(
                                        e2, hodCse, Role.ROLE_HOD, ActionType.APPROVE,
                                        "Proposal thoroughly reviewed and endorsed. Excellent alignment with department research goals.",
                                        EventStatus.PENDING_HOD, EventStatus.PENDING_DEAN, 1));

                        EventProposal e3 = new EventProposal();
                        e3.setTitle("Drone & Aerial Robotics Showcase");
                        e3.setDescription("Live outdoor demonstration of autonomous quadcopters, obstacle avoidance algorithms, and aerial photography by student club members.");
                        e3.setObjectives("Demonstrate flight control telemetry and autonomous waypoint tracking.");
                        e3.setCategory("Technical");
                        e3.setDepartment(ece);
                        e3.setOrganizer(student2);
                        e3.setFacultyCoordinator(facultyCoord);
                        e3.setVenue(amphitheatre);
                        e3.setStartDate(LocalDateTime.now().plusDays(20).withHour(14).withMinute(0));
                        e3.setEndDate(LocalDateTime.now().plusDays(20).withHour(18).withMinute(0));
                        e3.setEstimatedParticipants(150);
                        e3.setBudget(new BigDecimal("5000.00"));
                        e3.setBudgetBreakdown("Safety nets: ₹3,000; Battery recharge packs: ₹2,000");
                        e3.setStatus(EventStatus.CHANGES_REQUESTED);
                        e3.setCurrentStepOrder(1);
                        e3.setWorkflow(standardWorkflow);
                        e3.setAiCompletenessScore(68);
                        e3 = eventProposalRepository.save(e3);

                        approvalActionRepository.save(new ApprovalAction(
                                        e3, student2, Role.ROLE_STUDENT, ActionType.SUBMIT,
                                        "Submitted drone demonstration proposal.",
                                        EventStatus.DRAFT, EventStatus.PENDING_HOD, 1));
                        approvalActionRepository.save(new ApprovalAction(
                                        e3, hodEc, Role.ROLE_HOD, ActionType.REQUEST_CHANGES,
                                        "Please add a formal safety protocol and obtain written clearance from campus security for outdoor drone flight radius.",
                                        EventStatus.PENDING_HOD, EventStatus.CHANGES_REQUESTED, 1));

                        EventProposal e4 = new EventProposal();
                        e4.setTitle("Annual Inter-College Cultural Extravaganza");
                        e4.setDescription("Flagship cultural festival featuring music bands, classical dance recitals, theater drama, street plays, and art exhibitions across 2 days.");
                        e4.setObjectives("1. Promote student arts and music.\n2. Host inter-collegiate stage competitions.");
                        e4.setCategory("Cultural");
                        e4.setDepartment(cse);
                        e4.setOrganizer(student);
                        e4.setFacultyCoordinator(facultyCoord);
                        e4.setVenue(aud);
                        e4.setStartDate(LocalDateTime.now().plusDays(40).withHour(9).withMinute(0));
                        e4.setEndDate(LocalDateTime.now().plusDays(41).withHour(22).withMinute(0));
                        e4.setEstimatedParticipants(480);
                        e4.setBudget(new BigDecimal("45000.00"));
                        e4.setBudgetBreakdown("Sound & Stage: ₹20,000; Guest Judges: ₹15,000; Trophies & Certificates: ₹10,000");
                        e4.setStatus(EventStatus.APPROVED);
                        e4.setCurrentStepOrder(2);
                        e4.setWorkflow(standardWorkflow);
                        e4.setAiCompletenessScore(98);
                        e4.setAiSummary("Executive Summary: Flagship cultural fest. Fully endorsed by HOD CSE and sanctioned by Dean of Academics. Eligible for publication.");
                        e4 = eventProposalRepository.save(e4);

                        approvalActionRepository.save(new ApprovalAction(
                                        e4, student, Role.ROLE_STUDENT, ActionType.SUBMIT,
                                        "Cultural fest proposal with schedule and risk mitigation plan.",
                                        EventStatus.DRAFT, EventStatus.PENDING_HOD, 1));
                        approvalActionRepository.save(new ApprovalAction(
                                        e4, hodCse, Role.ROLE_HOD, ActionType.APPROVE,
                                        "Approved. Department budget committee cleared the proposal.",
                                        EventStatus.PENDING_HOD, EventStatus.PENDING_DEAN, 1));
                        approvalActionRepository.save(new ApprovalAction(
                                        e4, dean, Role.ROLE_DEAN, ActionType.APPROVE,
                                        "Final approval granted. Ensure security measures and faculty oversight on all stage performances.",
                                        EventStatus.PENDING_DEAN, EventStatus.APPROVED, 2));

                        EventProposal e5 = new EventProposal();
                        e5.setTitle("Cloud Architecture & DevOps Masterclass");
                        e5.setDescription("Intensive full-day workshop covering Kubernetes cluster orchestration, Docker containerization, CI/CD pipelines, and cloud security best practices.");
                        e5.setObjectives("1. Hands-on Docker and Kubernetes container deployment.\n2. Build automated GitHub Actions CI/CD pipeline.");
                        e5.setCategory("Workshop");
                        e5.setDepartment(cse);
                        e5.setOrganizer(student);
                        e5.setFacultyCoordinator(facultyCoord);
                        e5.setVenue(seminarHall);
                        e5.setStartDate(LocalDateTime.now().plusDays(8).withHour(9).withMinute(30));
                        e5.setEndDate(LocalDateTime.now().plusDays(8).withHour(16).withMinute(30));
                        e5.setEstimatedParticipants(120);
                        e5.setBudget(new BigDecimal("9500.00"));
                        e5.setBudgetBreakdown("Cloud Credits & Labs: ₹5,000; Lunch & Tea: ₹4,500");
                        e5.setStatus(EventStatus.PUBLISHED);
                        e5.setCurrentStepOrder(2);
                        e5.setWorkflow(standardWorkflow);
                        e5.setAiCompletenessScore(94);
                        e5.setAiSummary("Executive Summary: Technical hands-on masterclass. Approved and officially published to campus portal.");
                        e5 = eventProposalRepository.save(e5);

                        approvalActionRepository.save(new ApprovalAction(
                                        e5, student, Role.ROLE_STUDENT, ActionType.SUBMIT,
                                        "Workshop proposal submitted.",
                                        EventStatus.DRAFT, EventStatus.PENDING_HOD, 1));
                        approvalActionRepository.save(new ApprovalAction(
                                        e5, hodCse, Role.ROLE_HOD, ActionType.APPROVE,
                                        "Approved. Workshop lab requirements confirmed.",
                                        EventStatus.PENDING_HOD, EventStatus.PENDING_DEAN, 1));
                        approvalActionRepository.save(new ApprovalAction(
                                        e5, dean, Role.ROLE_DEAN, ActionType.APPROVE,
                                        "Approved for college-wide student registration.",
                                        EventStatus.PENDING_DEAN, EventStatus.APPROVED, 2));
                        approvalActionRepository.save(new ApprovalAction(
                                        e5, student, Role.ROLE_STUDENT, ActionType.PUBLISH,
                                        "Event officially published to college calendar.",
                                        EventStatus.APPROVED, EventStatus.PUBLISHED, 2));

                        registrationRepository.save(new EventRegistration(
                                        e5, student2, "Priya Patel", "priya.patel@college.edu", "21ECE042", "ECE",
                                        "TKT-5-88A19B"));
                }
        }
}
