package com.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.dashboard.repository.ProjectRepository;

@SpringBootTest
class ProjectRepositoryMappingTests {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Transactional(readOnly = true)
    void canReadProjectsThroughTheJpaMapping() {
        assertThat(projectRepository.count()).isGreaterThanOrEqualTo(0);
    }
}
