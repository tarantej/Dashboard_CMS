package com.dashboard.controller;

import com.dashboard.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

@Controller
public class PortfolioController
{
    private final ProjectService projectService;

    public PortfolioController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model) {

        model.addAttribute("projects", projectService.findAllProjects());

        // Breadcrumbs
        model.addAttribute("pageTitle", "All Projects");

        return "Dashboard/Portfolio/portfolio";
    }

    @GetMapping("/portfolio/{slug}")
    public String projectDetails(@PathVariable String slug, Model model) {

        return projectService.findProjectBySlug(slug)
                .map(project -> {
                    model.addAttribute("project", project);

                    // Breadcrumbs
                    model.addAttribute("pageTitle", project.getProjectTitle());

                    return "Dashboard/Portfolio/portfolio-detail";
                })
                .orElse("error/404");
    }
}