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

@Controller
public class ProjectController
{
    private final ProjectService projectService;
    private final TechnologyService technologyService;

    public ProjectController(
            ProjectService projectService,
            TechnologyService technologyService) {

        this.projectService = projectService;
        this.technologyService = technologyService;
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