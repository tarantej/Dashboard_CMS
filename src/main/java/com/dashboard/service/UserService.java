package com.dashboard.service;

import com.dashboard.DashboardUsers;
import com.dashboard.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dashboard.UserRole;
import com.dashboard.repository.UserRoleRepository;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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

    public Page<DashboardUsers> findUsers(
            String search,
            String role,
            Pageable pageable) {

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasRole = role != null && !role.isBlank();

        if (!hasRole) {
            return findUsers(search, pageable);
        }

        if (!hasSearch) {
            return userRepository.findByUserRole(role, pageable);
        }

        String searchTerm = search.trim();

        return userRepository
                .findByUserRoleAndUsernameContainingIgnoreCaseOrUserRoleAndEmailContainingIgnoreCase(
                        role,
                        searchTerm,
                        role,
                        searchTerm,
                        pageable);
    }

    public List<DashboardUsers> findUsers(
            String search,
            String role,
            String roleGroup,
            String provider) {

        Set<String> administratorRoles =
                Set.of("SUPER_ADMIN", "ADMINISTRATOR");

        List<String> availableRoles = userRoleRepository
                .findAllByOrderByIdAsc()
                .stream()
                .map(UserRole::getRole)
                .toList();

        List<String> groupRoles;

        if (roleGroup == null || roleGroup.isBlank()) {
            groupRoles = availableRoles;
        } else {
            switch (roleGroup.toLowerCase(Locale.ROOT)) {
                case "administrators":
                    groupRoles = availableRoles.stream()
                            .filter(administratorRoles::contains)
                            .toList();
                    break;

                case "employees":
                    groupRoles = availableRoles.stream()
                            .filter("EMPLOYEE"::equals)
                            .toList();
                    break;

                case "other":
                    groupRoles = availableRoles.stream()
                            .filter(roleName ->
                                    !administratorRoles.contains(roleName)
                                            && !"EMPLOYEE".equals(roleName))
                            .toList();
                    break;

                default:
                    return List.of();
            }
        }

        if (role != null && !role.isBlank()) {
            groupRoles = groupRoles.stream()
                    .filter(role::equals)
                    .toList();
        }

        if (groupRoles.isEmpty()) {
            return List.of();
        }

        String searchTerm =
                search == null ? "" : search.trim();

        String providerFilter =
                provider == null ? "" :
                        provider.trim().toUpperCase(Locale.ROOT);

        return userRepository.findByRolesSearchAndProvider(
                groupRoles,
                searchTerm,
                providerFilter);
    }

    public DashboardUsers getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    public DashboardUsers getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    public DashboardUsers updateUser(
            Integer id,
            DashboardUsers updatedUser) {

        DashboardUsers existingUser = getUserById(id);

        // Validate email uniqueness
        userRepository.findByEmail(updatedUser.getEmail())
                .ifPresent(user -> {
                    if (!user.getUserId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Email already exists");
                    }
                });

        // Validate role against the database
        boolean validRole = userRoleRepository
                .findAllByOrderByIdAsc()
                .stream()
                .anyMatch(role ->
                        role.getRole().equals(updatedUser.getUserRole()));

        if (!validRole) {
            throw new IllegalArgumentException("Invalid user role");
        }

        // Update permitted fields
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setUserRole(updatedUser.getUserRole());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setUserBio(updatedUser.getUserBio());
        existingUser.setUserCity(updatedUser.getUserCity());
        existingUser.setUserCountry(updatedUser.getUserCountry());

        // Update timestamp
        existingUser.setUpdatedAt(
                new Timestamp(System.currentTimeMillis()));

        return userRepository.save(existingUser);
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