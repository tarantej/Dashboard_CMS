package com.dashboard.repository;

import com.dashboard.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>
{
    List<Project> findAllByOrderByDisplayOrderAsc();

    boolean existsByProjectSlug(String projectSlug);

    boolean existsByProjectSlugAndProjectIdNot(
            String projectSlug,
            Integer projectId
    );

    List<Project> findTop3ByOrderByUpdatedAtDesc();
}