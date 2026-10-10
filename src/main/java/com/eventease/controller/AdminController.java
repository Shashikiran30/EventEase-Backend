package com.eventease.controller;

import com.eventease.dto.DashboardStatsDto;
import com.eventease.dto.UserDto;
import com.eventease.entity.Department;
import com.eventease.repository.DepartmentRepository;
import com.eventease.service.DashboardService;
import com.eventease.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    public AdminController(DashboardService dashboardService,
                           UserService userService,
                           DepartmentRepository departmentRepository) {
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        com.eventease.entity.User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser.getRole() != com.eventease.entity.Role.ROLE_ADMIN) {
            return ResponseEntity.status(403).body(java.util.Map.of("error", "Access denied: Requires administrator privileges."));
        }
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        com.eventease.entity.User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser.getRole() != com.eventease.entity.Role.ROLE_ADMIN) {
            return ResponseEntity.status(403).body(java.util.Map.of("error", "Access denied: Requires administrator privileges."));
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/departments")
    public ResponseEntity<?> getDepartments() {
        com.eventease.entity.User currentUser = userService.getCurrentAuthenticatedUser();
        if (currentUser.getRole() != com.eventease.entity.Role.ROLE_ADMIN) {
            return ResponseEntity.status(403).body(java.util.Map.of("error", "Access denied: Requires administrator privileges."));
        }
        return ResponseEntity.ok(departmentRepository.findAll());
    }
}
