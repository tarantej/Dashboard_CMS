package com.dashboard.controller;

import com.dashboard.Technology;
import com.dashboard.service.TechnologyService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class TechnologyController
{
    private final TechnologyService technologyService;

    public TechnologyController(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    // List and search technologies
    @GetMapping("/dashboard/technologies")
    public String listTechnologies(Model model) {


        model.addAttribute("technologies", technologyService.findAllTechnologies());
        model.addAttribute("pageTitle", "Technology");

        return "Dashboard/Technology/technology-index";
    }

    // Display Add Technology form
    @GetMapping("/dashboard/technologies/add")
    public String addTechnology(Model model) {

        model.addAttribute("technology", new Technology());
        model.addAttribute("pageTitle", "Add Technology");

        return "Dashboard/Technology/add-technology";
    }

    // Save new or edited technology
    @PostMapping("/dashboard/technologies/save")
    public String saveTechnology(
            @ModelAttribute("technology") Technology technology,
            Model model) {

        try {

            technologyService.saveTechnology(technology);

            return "redirect:/dashboard/technologies";

        } catch (IllegalArgumentException e) {

            model.addAttribute("pageTitle", "Add Technology");
            model.addAttribute("errorMessage", e.getMessage());

            return "Dashboard/Technology/add-technology";
        }
    }

    // Display Edit Technology form
    @GetMapping("/dashboard/technologies/edit/{id}")
    public String editTechnology(
            @PathVariable Integer id,
            Model model) {

        Optional<Technology> technology =
                technologyService.findTechnologyById(id);

        if (technology.isEmpty()) {
            return "redirect:/dashboard/technologies";
        }

        model.addAttribute("technology", technology.get());
        model.addAttribute("pageTitle", "Edit Technology");

        return "Dashboard/Technology/edit-technology";
    }

    // Update existing technology
    @PostMapping("/dashboard/technologies/update")
    public String updateTechnology(
            @ModelAttribute("technology") Technology technology,
            Model model) {

        try {

            technologyService.saveTechnology(technology);

            return "redirect:/dashboard/technologies";

        } catch (IllegalArgumentException e) {

            model.addAttribute("pageTitle", "Edit Technology");
            model.addAttribute("errorMessage", e.getMessage());

            return "Dashboard/Technology/edit-technology";
        }
    }
}