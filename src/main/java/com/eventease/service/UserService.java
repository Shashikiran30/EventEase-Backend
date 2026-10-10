package com.eventease.service;

import com.eventease.dto.UserDto;
import com.eventease.entity.Department;
import com.eventease.entity.Role;
import com.eventease.entity.User;
import com.eventease.repository.DepartmentRepository;
import com.eventease.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }

    public User getCurrentAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return userRepository.findById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
        }
        throw new RuntimeException("No authenticated user found in session");
    }

    public UserDto getUserDto(User user) {
        return UserDto.fromEntity(user, userRepository);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> UserDto.fromEntity(u, userRepository))
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(u -> UserDto.fromEntity(u, userRepository))
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByDepartment(Long departmentId) {
        return userRepository.findByDepartmentId(departmentId).stream()
                .map(u -> UserDto.fromEntity(u, userRepository))
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserDto.fromEntity(user, userRepository);
    }
}
