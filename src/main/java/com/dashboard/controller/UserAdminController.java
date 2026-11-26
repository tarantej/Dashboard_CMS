package com.dashboard.controller;

import com.dashboard.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;
import com.dashboard.DashboardUsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

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
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "") String roleGroup,
            @RequestParam(defaultValue = "") String provider,
            Model model) {

        List<DashboardUsers> allUsers =
                userService.findUsers(
                        search,
                        role,
                        roleGroup,
                        provider);

        List<UserRole> roles = userService.findAllRoles();

        model.addAttribute("allUsers", allUsers);
        model.addAttribute("roles", roles);
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedRoleGroup", roleGroup);
        model.addAttribute("selectedProvider", provider);
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

    @GetMapping("/admin/users/view/{id}")
    public String viewUser(
            @PathVariable Integer id,
            Model model,
            Authentication authentication) {

        // User whose profile is being viewed
        DashboardUsers user = userService.getUserById(id);

        // User currently logged in
        DashboardUsers currentUser =
                userService.getUserByUsername(authentication.getName());

        // Target user's profile details
        model.addAttribute("username", user.getUsername());

        model.addAttribute("first_name", user.getFirstName());
        model.addAttribute("last_name", user.getLastName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("user_role", user.getUserRole());

        model.addAttribute("phone_number", user.getPhoneNumber());
        model.addAttribute("bio", user.getUserBio());

        model.addAttribute("city", user.getUserCity());
        model.addAttribute("country", user.getUserCountry());

        model.addAttribute("profile_picture", user.getProfilePicture());
        model.addAttribute("oauth_provider", user.getOauthProvider());
        model.addAttribute("oauth_picture", user.getOauthPicture());

        model.addAttribute("created_at", user.getCreatedAt());
        model.addAttribute("updated_at", user.getUpdatedAt());

        // Authenticated user's dashboard identity
        model.addAttribute("currentUserFirstName", currentUser.getFirstName());
        model.addAttribute("currentUserProfilePicture",
                currentUser.getProfilePicture());

        // Keep the remaining target-user profile attributes here

        return "Dashboard/User/profile";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUser(
            @PathVariable Integer id,
            Model model) {

        DashboardUsers user = userService.getUserById(id);
        List<UserRole> roles = userService.findAllRoles();

        List<String> countries = getCountries();

        if (user.getUserCountry() != null
                && !user.getUserCountry().isBlank()
                && !countries.contains(user.getUserCountry())) {

            countries.add(0, user.getUserCountry());
        }



        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("countries", countries);


        model.addAttribute("pageTitle", "Edit User");

        return "Dashboard/User/edit-user";
    }

    private List<String> getCountries() {

        return Arrays.stream(Locale.getISOCountries())
                .map(code -> new Locale.Builder()
                        .setRegion(code)
                        .build()
                        .getDisplayCountry(Locale.ENGLISH))
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @PostMapping("/admin/users/update/{id}")
    public String updateUser(
            @PathVariable Integer id,
            @ModelAttribute("user") DashboardUsers user,
            RedirectAttributes redirectAttributes) {

        try {
            userService.updateUser(id, user);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "User updated successfully."
            );

            return "redirect:/admin/users";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

            return "redirect:/admin/users/edit/" + id;
        }
    }
}