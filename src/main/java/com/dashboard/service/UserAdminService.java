package com.dashboard.service;

import com.dashboard.DashboardUsers;
import com.dashboard.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserAdminService {

    private final UserRepository userRepository;

    public UserAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
