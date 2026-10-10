package com.eventease.service;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventease.config.JwtUtils;
import com.eventease.dto.LoginRequest;
import com.eventease.dto.LoginResponse;
import com.eventease.dto.RegisterRequest;
import com.eventease.dto.UserDto;
import com.eventease.entity.Department;
import com.eventease.entity.Role;
import com.eventease.entity.User;
import com.eventease.repository.DepartmentRepository;
import com.eventease.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       DepartmentRepository departmentRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public LoginResponse authenticate(LoginRequest request) {
        String identifier = request.getUsername() != null ? request.getUsername().trim() : "";
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateToken(user);
        return new LoginResponse(token, UserDto.fromEntity(user, userRepository));
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        if (request.getRole() != null && request.getRole() != Role.ROLE_STUDENT) {
            throw new IllegalArgumentException("Public registration is only allowed for student role");
        }
        if (request.getDepartmentId() == null) {
            throw new IllegalArgumentException("Department is required for student registration");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getEmail(),
                Role.ROLE_STUDENT,
                department,
                request.getDesignation()
        );
        user.setPhone(request.getPhone());

        User savedUser = userRepository.save(user);
        String token = jwtUtils.generateToken(savedUser);
        return new LoginResponse(token, UserDto.fromEntity(savedUser, userRepository));
    }
}

