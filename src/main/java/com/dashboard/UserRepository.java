package com.dashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<DashboardUsers, Integer>
{
    // Find Username

    Optional<DashboardUsers> findByUsername(String username);

    //  Find User Email

    Optional<DashboardUsers> findByEmail(String email);

    //  Find by Reset Token

    Optional<DashboardUsers> findByResetToken(String resetToken);
}