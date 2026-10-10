-- ==============================================================================
-- EventEase Initial Seed Data (Compatible with MySQL / Aiven MySQL)
-- ==============================================================================

-- 1. Insert Departments
INSERT IGNORE INTO `departments` (`id`, `code`, `name`, `description`) VALUES
(1, 'CSE', 'Computer Science and Engineering', 'Department of Computer Science & Engineering'),
(2, 'ECE', 'Electronics and Communication Engineering', 'Department of Electronics & Communication'),
(3, 'ME', 'Mechanical Engineering', 'Department of Mechanical Engineering'),
(4, 'CE', 'Civil Engineering', 'Department of Civil Engineering'),
(5, 'IT', 'Information Technology', 'Department of Information Technology'),
(6, 'EEE', 'Electrical and Electronics Engineering', 'Department of Electrical & Electronics'),
(7, 'MBA', 'Master of Business Administration', 'Department of Management Studies');

-- 2. Insert Standard Venues
INSERT IGNORE INTO `venues` (`id`, `name`, `location`, `capacity`, `facilities`, `active`) VALUES
(1, 'Main University Auditorium', 'Central Block - Ground Floor', 800, 'Dolby Audio, Dual 4K Projectors, Stage Lighting, AC, Podium', 1),
(2, 'Seminar Hall A', 'CSE Block - 3rd Floor', 150, 'Smart Board, High-speed Wi-Fi, Wireless Mic, AC', 1),
(3, 'Open Air Amphitheatre', 'North Campus Garden', 1200, 'Open Stage, High-power PA system, Ambient Floodlights', 1),
(4, 'Innovation Hub Lab', 'R&D Center - 2nd Floor', 80, 'Workstations, 3D Printers, High-speed LAN, Presentation Screen', 1),
(5, 'Sports Complex Indoor Court', 'Sports Arena', 500, 'Scoreboard, Sound System, Bleacher Seating, Changing Rooms', 1);

-- 3. Insert Standard Approval Workflows
INSERT IGNORE INTO `approval_workflows` (`id`, `name`, `description`, `event_type`, `active`) VALUES
(1, 'Standard Academic & Technical Workflow', '3-Tier review for standard department and technical events', 'STANDARD', 1),
(2, 'High Budget & Major Cultural Workflow', '4-Tier comprehensive approval for campus-wide festivals and high-budget events', 'CULTURAL', 1);

-- 4. Insert Approval Steps for Workflows
-- Steps for Workflow 1: Standard
INSERT IGNORE INTO `approval_steps` (`id`, `workflow_id`, `step_order`, `step_name`, `required_role`, `is_mandatory`) VALUES
(1, 1, 1, 'Faculty Coordinator & HOD Endorsement', 'ROLE_HOD', 1),
(2, 1, 2, 'Dean of Student Affairs Review', 'ROLE_DEAN', 1),
(3, 1, 3, 'Principal / Administrative Signoff', 'ROLE_PRINCIPAL', 1);

-- Steps for Workflow 2: Cultural / High Budget
INSERT IGNORE INTO `approval_steps` (`id`, `workflow_id`, `step_order`, `step_name`, `required_role`, `is_mandatory`) VALUES
(4, 2, 1, 'HOD Academic Verification', 'ROLE_HOD', 1),
(5, 2, 2, 'Finance Officer Budget Clearance', 'ROLE_FINANCE_OFFICER', 1),
(6, 2, 3, 'Dean Student Welfare Clearance', 'ROLE_DEAN', 1),
(7, 2, 4, 'Principal Final Authorization', 'ROLE_PRINCIPAL', 1);
