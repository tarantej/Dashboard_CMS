package com.dashboard.service;

import com.dashboard.DashboardUsers;
import com.dashboard.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dashboard.UserRole;
import com.dashboard.repository.UserRoleRepository;
import java.util.List;

import com.dashboard.repository.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.sql.Timestamp;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Supplies the paginated user list for the future administration screens.
     * Authentication and personal-profile operations remain in their existing services.
     */
    public Page<DashboardUsers> findUsers(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return userRepository.findAll(pageable);
        }

        String searchTerm = search.trim();
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                searchTerm,
                searchTerm,
                pageable);
    }

    public List<UserRole> findAllRoles() {
        return userRoleRepository.findAllByOrderByIdAsc();
    }

    public DashboardUsers createUser(DashboardUsers user) {

        // Check username
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Validate role
        boolean validRole = userRoleRepository.findAllByOrderByIdAsc()
                .stream()
                .anyMatch(role -> role.getRole().equals(user.getUserRole()));

        if (!validRole) {
            throw new IllegalArgumentException("Invalid user role");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set timestamps
        Timestamp now = new Timestamp(System.currentTimeMillis());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Save user
        return userRepository.save(user);
    }

}