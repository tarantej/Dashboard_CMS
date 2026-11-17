package com.dashboard.controller;

import com.dashboard.Project;
import com.dashboard.ProjectStatus;
import com.dashboard.ProjectType;
import com.dashboard.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import com.dashboard.Technology;
import com.dashboard.service.TechnologyService;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

import com.dashboard.service.MediaService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
public class ProjectController
{
    private final ProjectService projectService;
    private final TechnologyService technologyService;
    private final MediaService mediaService;

    public ProjectController(
            ProjectService projectService,
            TechnologyService technologyService,
            MediaService mediaService) {

        this.projectService = projectService;
        this.technologyService = technologyService;
        this.mediaService = mediaService;
    }



    @GetMapping("/dashboard/projects")
    public String listProjects(Model model) {

        model.addAttribute("projects", projectService.findAllProjectsByDisplayOrder());
        model.addAttribute("pageTitle", "Projects");

        return "Dashboard/Project/project-index";
    }

    @GetMapping("/dashboard/projects/add")
    public String addProject(Model model) {

        model.addAttribute("project", new Project());
        populateProjectForm(model, "Add Project");

        return "Dashboard/Project/add-project";
    }

    @PostMapping("/dashboard/projects/save")
    public String saveProject(
            @ModelAttribute("project") Project project,
            @RequestParam Integer statusId,
            @RequestParam Integer typeId,
            @RequestParam(required = false) List<Integer> technologyIds,
            @RequestParam(required = false) MultipartFile projectLogoFile,
            @RequestParam(required = false) MultipartFile projectFeatureImageFile,
            Model model) {

        Optional<ProjectStatus> status =
                projectService.findProjectStatusById(statusId);

        Optional<ProjectType> type =
                projectService.findProjectTypeById(typeId);

        if (status.isEmpty() || type.isEmpty()) {
            populateProjectForm(
                    model,
                    project.getProjectId() == null ? "Add Project" : "Edit Project"
            );

            model.addAttribute("errorMessage", "Invalid project status or type.");

            return project.getProjectId() == null
                    ? "Dashboard/Project/add-project"
                    : "Dashboard/Project/edit-project";
        }

        // Retrieve existing project when editing
        Project existingProject = null;

        if (project.getProjectId() != null) {
            Optional<Project> existing =
                    projectService.findProjectById(project.getProjectId());

            if (existing.isEmpty()) {
                return "redirect:/dashboard/projects";
            }

            existingProject = existing.get();

            // Preserve existing image paths
            project.setProjectLogo(existingProject.getProjectLogo());
            project.setProjectFeatureImage(existingProject.getProjectFeatureImage());
        }

        project.setProjectStatus(status.get());
        project.setProjectType(type.get());

        Set<Technology> selectedTechnologies = new HashSet<>();

        if (technologyIds != null) {

            for (Integer technologyId : technologyIds) {

                Optional<Technology> technology =
                        technologyService.findTechnologyById(technologyId);

                if (technology.isEmpty()) {

                    populateProjectForm(
                            model,
                            project.getProjectId() == null ? "Add Project" : "Edit Project"
                    );

                    model.addAttribute("errorMessage", "Invalid technology selection.");

                    return project.getProjectId() == null
                            ? "Dashboard/Project/add-project"
                            : "Dashboard/Project/edit-project";
                }

                selectedTechnologies.add(technology.get());
            }
        }

        project.setTechnologies(selectedTechnologies);

        if (project.getProjectSlug() == null ||
                project.getProjectSlug().isBlank()) {

            project.setProjectSlug(
                    project.getProjectTitle()
                            .toLowerCase()
                            .trim()
                            .replaceAll("[^a-z0-9]+", "-")
                            .replaceAll("^-|-$", "")
            );
        }

        if (!projectService.isSlugAvailable(
                project.getProjectSlug(), project.getProjectId())) {

            populateProjectForm(
                    model,
                    project.getProjectId() == null ? "Add Project" : "Edit Project"
            );

            model.addAttribute("errorMessage", "This project slug is already in use.");

            return project.getProjectId() == null
                    ? "Dashboard/Project/add-project"
                    : "Dashboard/Project/edit-project";
        }

        String newLogoPath = null;
        String newFeatureImagePath = null;

        try {

            // Upload replacement logo if selected
            if (projectLogoFile != null && !projectLogoFile.isEmpty()) {
                newLogoPath = mediaService.storeProjectLogo(projectLogoFile);
                project.setProjectLogo(newLogoPath);
            }

            // Upload replacement featured image if selected
            if (projectFeatureImageFile != null && !projectFeatureImageFile.isEmpty()) {
                newFeatureImagePath =
                        mediaService.storeFeaturedImage(projectFeatureImageFile);

                project.setProjectFeatureImage(newFeatureImagePath);
            }

            projectService.saveProject(project);

        } catch (IOException | IllegalArgumentException exception) {

            // Clean up any newly uploaded files if the operation fails
            try {
                if (newLogoPath != null) {
                    mediaService.deleteMedia(newLogoPath);
                }

                if (newFeatureImagePath != null) {
                    mediaService.deleteMedia(newFeatureImagePath);
                }
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            populateProjectForm(
                    model,
                    project.getProjectId() == null ? "Add Project" : "Edit Project"
            );

            model.addAttribute("errorMessage", exception.getMessage());

            return project.getProjectId() == null
                    ? "Dashboard/Project/add-project"
                    : "Dashboard/Project/edit-project";
        }

        return "redirect:/dashboard/projects";
    }

    @GetMapping("/dashboard/projects/edit/{id}")
    public String editProject(@PathVariable Integer id, Model model) {

        Optional<Project> project = projectService.findProjectById(id);

        if (project.isEmpty()) {
            return "redirect:/dashboard/projects";
        }

        model.addAttribute("project", project.get());
        populateProjectForm(model, "Edit Project");

        return "Dashboard/Project/edit-project";
    }

    @PostMapping("/dashboard/projects/delete/{id}")
    public String deleteProject(@PathVariable Integer id) {

        projectService.deleteProject(id);

        return "redirect:/dashboard/projects";
    }


    private void populateProjectForm(Model model, String pageTitle) {

        model.addAttribute("statuses", projectService.findAllProjectStatuses());
        model.addAttribute("types", projectService.findAllProjectTypes());
        model.addAttribute("technologies", technologyService.findAllTechnologies());
        model.addAttribute("pageTitle", pageTitle);
    }
}