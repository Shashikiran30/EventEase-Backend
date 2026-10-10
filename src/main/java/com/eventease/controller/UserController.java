package com.eventease.controller;

import com.eventease.dto.UserDto;
import com.eventease.entity.Role;
import com.eventease.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for event proposals to select faculty coordinators.
     * Accessible by authenticated users.
     */
    @GetMapping("/coordinators")
    public ResponseEntity<List<UserDto>> getCoordinators() {
        List<UserDto> coordinators = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.ROLE_FACULTY_COORDINATOR || u.getRole() == Role.ROLE_HOD)
                .collect(Collectors.toList());
        return ResponseEntity.ok(coordinators);
    }
}
