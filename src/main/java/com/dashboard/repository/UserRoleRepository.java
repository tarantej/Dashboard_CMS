package com.dashboard.repository;

import com.dashboard.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

    List<UserRole> findAllByOrderByIdAsc();
}
