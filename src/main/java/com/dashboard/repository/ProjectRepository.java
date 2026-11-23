package com.dashboard.repository;

import com.dashboard.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>
{
    List<Project> findAllByOrderByDisplayOrderAsc();

    boolean existsByProjectSlug(String projectSlug);

    boolean existsByProjectSlugAndProjectIdNot(
            String projectSlug,
            Integer projectId
    );

    @EntityGraph(attributePaths = "technologies")
    List<Project> findTop3ByOrderByUpdatedAtDesc();

    Optional<Project> findByProjectSlug(String projectSlug);

    List<Project> findAllByPublishStatusOrderByDisplayOrderAsc(
            String publishStatus
    );

    Optional<Project> findByProjectSlugAndPublishStatus(
            String projectSlug,
            String publishStatus
    );

    List<Project> findTop3ByPublishStatusOrderByUpdatedAtDesc(
            String publishStatus
    );
}