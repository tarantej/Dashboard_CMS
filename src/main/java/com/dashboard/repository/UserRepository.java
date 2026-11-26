package com.dashboard.repository;

import com.dashboard.DashboardUsers;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
    SELECT u FROM DashboardUsers u
    WHERE u.userRole IN :roles
    AND (
        :provider = ''
        OR UPPER(COALESCE(NULLIF(TRIM(u.oauthProvider), ''), 'LOCAL'))
           = UPPER(:provider)
    )
    AND (
        :search = ''
        OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
    )
    """)
    List<DashboardUsers> findByRolesSearchAndProvider(
            @Param("roles") List<String> roles,
            @Param("search") String search,
            @Param("provider") String provider);

}