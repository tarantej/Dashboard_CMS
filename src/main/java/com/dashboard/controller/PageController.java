
package com.dashboard.controller;

import com.dashboard.Page;
import com.dashboard.service.PageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard/pages")
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    // All Pages
    @GetMapping
    public String listPages(Model model) {
        model.addAttribute("pages", pageService.getAllPages());
        return "Dashboard/Pages/pages-index";
    }

    // Add New Page
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("page", new Page());
        return "Dashboard/Pages/add_new_page";
    }

    // Save Page
    @PostMapping("/save")
    public String savePage(@ModelAttribute("page") Page page) {
        pageService.createPage(page);
        return "redirect:/dashboard/pages";
    }

    // Edit Page
    @GetMapping("/edit/{id}")
    public String editPage(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute("page", pageService.getPageById(id));
        return "Dashboard/Pages/edit_page";
    }

    // Update Page
    @PostMapping("/update/{id}")
    public String updatePage(
            @PathVariable Integer id,
            @ModelAttribute("page") Page page) {

        pageService.updatePage(id, page);
        return "redirect:/dashboard/pages";
    }

    // Move to Trash
    @PostMapping("/trash/{id}")
    public String trashPage(@PathVariable Integer id) {
        pageService.moveToTrash(id);
        return "redirect:/dashboard/pages";
    }

    // Restore Page
    @PostMapping("/restore/{id}")
    public String restorePage(@PathVariable Integer id) {
        pageService.restorePage(id);
        return "redirect:/dashboard/pages";
    }

    // Permanently Delete
    @PostMapping("/delete/{id}")
    public String deletePage(@PathVariable Integer id) {
        pageService.deletePage(id);
        return "redirect:/dashboard/pages";
    }
}