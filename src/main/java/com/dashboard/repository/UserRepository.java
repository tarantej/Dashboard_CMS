package com.dashboard.repository;

import com.dashboard.DashboardUsers;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<DashboardUsers, Integer> {

    Optional<DashboardUsers> findByUsername(String username);

    Optional<DashboardUsers> findByEmail(String email);

    Optional<DashboardUsers> findByResetToken(String resetToken);

    Page<DashboardUsers> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username,
            String email,
            Pageable pageable);

    Page<DashboardUsers> findByUserRole(
            String userRole,
            Pageable pageable);

    Page<DashboardUsers> findByUserRoleAndUsernameContainingIgnoreCaseOrUserRoleAndEmailContainingIgnoreCase(
            String role1,
            String username,
            String role2,
            String email,
            Pageable pageable);
}