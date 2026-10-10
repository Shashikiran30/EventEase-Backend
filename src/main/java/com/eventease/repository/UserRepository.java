package com.eventease.repository;

import com.eventease.entity.Role;
import com.eventease.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<Role> findRoleById(Long id);
    List<User> findByRole(Role role);
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByRoleAndDepartmentId(Role role, Long departmentId);
}
