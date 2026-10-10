package com.eventease.controller;

import com.eventease.entity.Department;
import com.eventease.repository.DepartmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;
    private final com.eventease.repository.UserRepository userRepository;

    public DepartmentController(DepartmentRepository departmentRepository,
                                com.eventease.repository.UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Public endpoint — returns all departments for use in registration form dropdown.
     * No authentication required.
     */
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> list = departmentRepository.findAll();
        for (Department d : list) {
            userRepository.findByRoleAndDepartmentId(com.eventease.entity.Role.ROLE_HOD, d.getId())
                    .stream().findFirst().ifPresent(h -> d.setHodName(h.getFullName()));
        }
        return ResponseEntity.ok(list);
    }
}
