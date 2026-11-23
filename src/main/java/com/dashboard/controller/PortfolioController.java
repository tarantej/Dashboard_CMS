package com.dashboard.controller;

import com.dashboard.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class PortfolioController
{
    private final ProjectService projectService;

    public PortfolioController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/portfolio")
    public String portfolio(Model model) {

        model.addAttribute("projects", projectService.findPublishedProjects());

        // Breadcrumbs
        model.addAttribute("pageTitle", "All Projects");

        return "Dashboard/Portfolio/portfolio";
    }

    @GetMapping("/portfolio/{slug}")
    public String projectDetails(@PathVariable String slug, Model model) {

        return projectService.findPublishedProjectBySlug(slug)
                .map(project -> {
                    model.addAttribute("project", project);
                    model.addAttribute("pageTitle", project.getProjectTitle());

                    return "Dashboard/Portfolio/portfolio-detail";
                })
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND)
                );
    }
}