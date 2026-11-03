package com.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import com.dashboard.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class UserAdminServiceTests {

    @Autowired
    private UserService userService;

    @Test
    @Transactional(readOnly = true)
    void listsUsersWithoutChangingAuthenticationData() {
        var users = userService.findUsers(null, PageRequest.of(0, 10));

        assertThat(users).isNotNull();
        assertThat(users.getContent()).doesNotContainNull();
    }
}