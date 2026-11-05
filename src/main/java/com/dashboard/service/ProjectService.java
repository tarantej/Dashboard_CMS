package com.dashboard.service;

import com.dashboard.Project;
import com.dashboard.repository.ProjectRepository;
import com.dashboard.ProjectStatus;
import com.dashboard.ProjectType;
import com.dashboard.repository.ProjectStatusRepo;
import com.dashboard.repository.ProjectTypeRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService
{
    private final ProjectRepository projectRepository;
    private final ProjectStatusRepo projectStatusRepo;
    private final ProjectTypeRepo projectTypeRepo;

    public ProjectService(ProjectRepository projectRepository, ProjectStatusRepo projectStatusRepo, ProjectTypeRepo projectTypeRepo) {
        this.projectRepository = projectRepository;
        this.projectStatusRepo = projectStatusRepo;
        this.projectTypeRepo = projectTypeRepo;
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public Page<Project> findAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    public Optional<Project> findProjectById(Integer id) {
        return projectRepository.findById(id);
    }

    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Integer id) {
        projectRepository.deleteById(id);
    }

    public List<ProjectStatus> findAllProjectStatuses() {
        return projectStatusRepo.findAll();
    }

    public List<ProjectType> findAllProjectTypes() {
        return projectTypeRepo.findAll();
    }

    public List<Project> findAllProjectsByDisplayOrder() {
        return projectRepository.findAllByOrderByDisplayOrderAsc();
    }

    public boolean isSlugAvailable(String slug, Integer projectId) {
        if (projectId == null) {
            return !projectRepository.existsByProjectSlug(slug);
        }

        return !projectRepository.existsByProjectSlugAndProjectIdNot(slug, projectId);
    }

    public Optional<ProjectStatus> findProjectStatusById(Integer id) {
        return projectStatusRepo.findById(id);
    }

    public Optional<ProjectType> findProjectTypeById(Integer id) {
        return projectTypeRepo.findById(id);
    }
}