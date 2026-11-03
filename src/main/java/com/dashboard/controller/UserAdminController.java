package com.dashboard.controller;

import com.dashboard.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.dashboard.DashboardUsers;

import com.dashboard.UserRole;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserAdminController
{
    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String userIndex(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10);

        Page<DashboardUsers> users =
                userService.findUsers(search, pageable);

        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Users");

        return "Dashboard/User/users-index";
    }

    @GetMapping("/admin/users/add")
    public String addUser(Model model) {

        List<UserRole> roles = userService.findAllRoles();

        model.addAttribute("roles", roles);
        model.addAttribute("user", new DashboardUsers());
        model.addAttribute("pageTitle", "Add New User");

        return "Dashboard/User/add-user";
    }

    @PostMapping("/admin/users/save")
    public String saveUser(
            @ModelAttribute("user") DashboardUsers user,
            RedirectAttributes redirectAttributes) {

        try {

            userService.createUser(user);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User created successfully."
            );

            return "redirect:/admin/users";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "redirect:/admin/users/add";
        }
    }
}