-- ==============================================================================
-- EventEase MySQL Database Schema Migration
-- Compatible with MySQL 8.0+ / Aiven MySQL / Cloud DB
-- ==============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1. DEPARTMENTS TABLE
CREATE TABLE IF NOT EXISTS `departments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(50) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` VARCHAR(500) NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_departments_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. USERS TABLE
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(100) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    `department_id` BIGINT NULL,
    `designation` VARCHAR(100) NULL,
    `phone` VARCHAR(30) NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_username` (`username`),
    UNIQUE KEY `uk_users_email` (`email`),
    KEY `fk_users_department` (`department_id`),
    CONSTRAINT `fk_users_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. VENUES TABLE
CREATE TABLE IF NOT EXISTS `venues` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `location` VARCHAR(255) NOT NULL,
    `capacity` INT NULL,
    `facilities` VARCHAR(1000) NULL,
    `active` BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_venues_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. APPROVAL WORKFLOWS TABLE
CREATE TABLE IF NOT EXISTS `approval_workflows` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `description` VARCHAR(500) NULL,
    `event_type` VARCHAR(100) NULL,
    `active` BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_workflows_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. APPROVAL STEPS TABLE
CREATE TABLE IF NOT EXISTS `approval_steps` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `workflow_id` BIGINT NOT NULL,
    `step_order` INT NOT NULL,
    `step_name` VARCHAR(255) NOT NULL,
    `required_role` VARCHAR(50) NOT NULL,
    `is_mandatory` BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (`id`),
    KEY `fk_steps_workflow` (`workflow_id`),
    CONSTRAINT `fk_steps_workflow` FOREIGN KEY (`workflow_id`) REFERENCES `approval_workflows` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. EVENTS / EVENT PROPOSALS TABLE
CREATE TABLE IF NOT EXISTS `events` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT NOT NULL,
    `objectives` TEXT NULL,
    `category` VARCHAR(100) NOT NULL,
    `department_id` BIGINT NOT NULL,
    `organizer_id` BIGINT NOT NULL,
    `coordinator_id` BIGINT NULL,
    `venue_id` BIGINT NOT NULL,
    `start_date` DATETIME NOT NULL,
    `end_date` DATETIME NOT NULL,
    `estimated_participants` INT NULL,
    `budget` DECIMAL(12,2) DEFAULT 0.00,
    `budget_breakdown` TEXT NULL,
    `document_urls` TEXT NULL,
    `status` VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    `workflow_id` BIGINT NULL,
    `current_step_order` INT DEFAULT 1,
    `ai_summary` TEXT NULL,
    `ai_completeness_score` INT DEFAULT 80,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_events_department` (`department_id`),
    KEY `fk_events_organizer` (`organizer_id`),
    KEY `fk_events_coordinator` (`coordinator_id`),
    KEY `fk_events_venue` (`venue_id`),
    KEY `fk_events_workflow` (`workflow_id`),
    CONSTRAINT `fk_events_department` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
    CONSTRAINT `fk_events_organizer` FOREIGN KEY (`organizer_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_events_coordinator` FOREIGN KEY (`coordinator_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_events_venue` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`),
    CONSTRAINT `fk_events_workflow` FOREIGN KEY (`workflow_id`) REFERENCES `approval_workflows` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. APPROVAL ACTIONS (AUDIT TRAIL) TABLE
CREATE TABLE IF NOT EXISTS `approval_actions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `event_id` BIGINT NOT NULL,
    `actor_id` BIGINT NOT NULL,
    `actor_role` VARCHAR(50) NOT NULL,
    `action` VARCHAR(50) NOT NULL,
    `comments` VARCHAR(1000) NULL,
    `previous_status` VARCHAR(50) NULL,
    `new_status` VARCHAR(50) NULL,
    `step_order` INT NULL,
    `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_actions_event` (`event_id`),
    KEY `fk_actions_actor` (`actor_id`),
    CONSTRAINT `fk_actions_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_actions_actor` FOREIGN KEY (`actor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. EVENT REGISTRATIONS TABLE
CREATE TABLE IF NOT EXISTS `event_registrations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `event_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `student_name` VARCHAR(255) NOT NULL,
    `student_email` VARCHAR(255) NOT NULL,
    `student_roll_no` VARCHAR(100) NULL,
    `student_department` VARCHAR(100) NULL,
    `ticket_code` VARCHAR(100) NOT NULL,
    `registered_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status` VARCHAR(50) DEFAULT 'CONFIRMED',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_registrations_ticket` (`ticket_code`),
    UNIQUE KEY `uk_registrations_event_user` (`event_id`, `user_id`),
    KEY `fk_registrations_user` (`user_id`),
    CONSTRAINT `fk_registrations_event` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_registrations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. NOTIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS `notifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `recipient_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `message` TEXT NOT NULL,
    `type` VARCHAR(50) NULL,
    `related_event_id` BIGINT NULL,
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `fk_notifications_recipient` (`recipient_id`),
    CONSTRAINT `fk_notifications_recipient` FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
