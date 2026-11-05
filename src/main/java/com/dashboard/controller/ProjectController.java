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

import java.util.Optional;

@Controller
public class ProjectController
{
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
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
        model.addAttribute("statuses", projectService.findAllProjectStatuses());
        model.addAttribute("types", projectService.findAllProjectTypes());
        model.addAttribute("pageTitle", "Add Project");

        return "Dashboard/Project/add-project";
    }

    @PostMapping("/dashboard/projects/save")
    public String saveProject(
            @ModelAttribute("project") Project project,
            @RequestParam Integer statusId,
            @RequestParam Integer typeId,
            Model model) {

        Optional<ProjectStatus> status =
                projectService.findProjectStatusById(statusId);

        Optional<ProjectType> type =
                projectService.findProjectTypeById(typeId);

        if (status.isEmpty() || type.isEmpty()) {
            model.addAttribute("statuses", projectService.findAllProjectStatuses());
            model.addAttribute("types", projectService.findAllProjectTypes());
            model.addAttribute("errorMessage", "Invalid project status or type.");

            return project.getProjectId() == null
                    ? "Dashboard/Project/add-project"
                    : "Dashboard/Project/edit-project";
        }

        project.setProjectStatus(status.get());
        project.setProjectType(type.get());

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

            model.addAttribute("statuses", projectService.findAllProjectStatuses());
            model.addAttribute("types", projectService.findAllProjectTypes());
            model.addAttribute("errorMessage", "This project slug is already in use.");

            return project.getProjectId() == null
                    ? "Dashboard/Project/add-project"
                    : "Dashboard/Project/edit-project";
        }

        projectService.saveProject(project);

        return "redirect:/dashboard/projects";
    }

    @GetMapping("/dashboard/projects/edit/{id}")
    public String editProject(@PathVariable Integer id, Model model) {

        Optional<Project> project = projectService.findProjectById(id);

        if (project.isEmpty()) {
            return "redirect:/dashboard/projects";
        }

        model.addAttribute("project", project.get());
        model.addAttribute("statuses", projectService.findAllProjectStatuses());
        model.addAttribute("types", projectService.findAllProjectTypes());
        model.addAttribute("pageTitle", "Edit Project");

        return "Dashboard/Project/edit-project";
    }

    @PostMapping("/dashboard/projects/delete/{id}")
    public String deleteProject(@PathVariable Integer id) {

        projectService.deleteProject(id);

        return "redirect:/dashboard/projects";
    }
}