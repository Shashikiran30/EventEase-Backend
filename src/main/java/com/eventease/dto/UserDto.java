package com.eventease.dto;

import com.eventease.entity.Role;
import com.eventease.entity.User;

public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private String designation;
    private String phone;
    private String hodName;
    private String deanName;

    public UserDto() {}

    public static UserDto fromEntity(User user) {
        return fromEntity(user, null);
    }

    public static UserDto fromEntity(User user, com.eventease.repository.UserRepository userRepository) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        if (user.getDepartment() != null) {
            dto.setDepartmentId(user.getDepartment().getId());
            dto.setDepartmentName(user.getDepartment().getName());
            dto.setDepartmentCode(user.getDepartment().getCode());
        }
        dto.setDesignation(user.getDesignation());
        dto.setPhone(user.getPhone());

        if (userRepository != null) {
            if (user.getDepartment() != null) {
                userRepository.findByRoleAndDepartmentId(Role.ROLE_HOD, user.getDepartment().getId())
                        .stream().findFirst().ifPresent(hod -> dto.setHodName(hod.getFullName()));
            }
            userRepository.findByRole(Role.ROLE_DEAN)
                    .stream().findFirst().ifPresent(dean -> dto.setDeanName(dean.getFullName()));
        }
        return dto;
    }

    public String getHodName() {
        return hodName;
    }

    public void setHodName(String hodName) {
        this.hodName = hodName;
    }

    public String getDeanName() {
        return deanName;
    }

    public void setDeanName(String deanName) {
        this.deanName = deanName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
