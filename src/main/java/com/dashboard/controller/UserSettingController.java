package com.dashboard.controller;

import java.io.IOException;

import com.dashboard.DashboardUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.sql.Timestamp;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.dashboard.repository.UserRepository;



@Controller
public class UserSettingController
{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/profile")
    public String UserProfile(Authentication authentication, Model model)
    {

//        System.out.println("================================");
//        System.out.println("Authentication Name : "
//                + authentication.getName());
//
//        System.out.println("Principal Class : "
//                + authentication.getPrincipal().getClass());
//
//        System.out.println("Principal : "
//                + authentication.getPrincipal());
//
//        System.out.println("================================");

        DashboardUsers user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //  Get Session Username
        model.addAttribute("username", authentication.getName());

        //  Get profile details

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





        //  Breadcrumbs

        model.addAttribute("pageTitle", "Profile");

        return "Dashboard/User/profile";
    }

    @PostMapping("/profile")
    public String UserUpdateProfile(
            Authentication authentication,
            @RequestParam("profilePicture") MultipartFile file)
            throws IOException {

        if (file.isEmpty()) {
            return "redirect:/profile";
        }

        DashboardUsers user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String filename =
                System.currentTimeMillis() + "_"
                        + file.getOriginalFilename();

        Path uploadPath = Paths.get(
                "src/main/resources/static/uploads/profile");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(
                file.getInputStream(),
                uploadPath.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);

        user.setProfilePicture(filename);
        userRepository.save(user);

        return "redirect:/profile";
    }

    @GetMapping("/account-settings")
    public String UserAccountSettings(Authentication authentication, Model model)
    {
        DashboardUsers user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //  Get Session Username
        model.addAttribute("username", authentication.getName());

        // Account Information
        model.addAttribute("first_name", user.getFirstName());
        model.addAttribute("last_name", user.getLastName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("phone_number", user.getPhoneNumber());
        model.addAttribute("bio", user.getUserBio());

        // Location Information
        model.addAttribute("city", user.getUserCity());
        model.addAttribute("country", user.getUserCountry());

        //  Breadcrumbs

        model.addAttribute("pageTitle", "Account Settings");

        return "Dashboard/User/account-settings";
    }

    @PostMapping("/account-settings")
    public String UserUpdateAccountSettings(
            Authentication authentication,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("bio") String bio,
            @RequestParam("city") String city,
            @RequestParam("country") String country,
            RedirectAttributes redirectAttributes) {

        // Retrieve authenticated user
        DashboardUsers user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Trim submitted values
        firstName = firstName.trim();
        lastName = lastName.trim();
        email = email.trim().toLowerCase();

        // Validate required fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "First name, last name and email are required.");

            return "redirect:/account-settings";
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please enter a valid email address.");

            return "redirect:/account-settings";
        }

        // Check whether another user already uses this email
        var existingEmail = userRepository.findByEmail(email);

        if (existingEmail.isPresent()
                && !existingEmail.get().getUserId().equals(user.getUserId())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This email address is already registered.");

            return "redirect:/account-settings";
        }

        // Update account information
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber.trim());
        user.setUserBio(bio.trim());
        user.setUserCity(city.trim());
        user.setUserCountry(country.trim());

        // Update timestamp
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        // Save changes
        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your account information has been updated successfully.");

        return "redirect:/account-settings";

    }

    @PostMapping("/account-settings/change-password")
    public String changePassword(
            Authentication authentication,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        DashboardUsers user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check whether the account has a locally stored password
        if (user.getPassword() == null || user.getPassword().isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This account does not have a local password.");

            return "redirect:/account-settings";
        }

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Your current password is incorrect.");

            return "redirect:/account-settings";
        }

        // Validate new password length
        if (newPassword.length() < 8) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Your new password must contain at least 8 characters.");

            return "redirect:/account-settings";
        }

        // Ensure new password is different
        if (passwordEncoder.matches(newPassword, user.getPassword())) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Your new password must be different from your current password.");

            return "redirect:/account-settings";
        }

        // Confirm new password
        if (!newPassword.equals(confirmPassword)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "New password and confirmation do not match.");

            return "redirect:/account-settings";
        }

        // Encode and save new password
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "passwordSuccessMessage",
                "Your password has been updated successfully.");

        return "redirect:/account-settings";
    }

    @GetMapping("/system-settings")
    public String UserSystemSettings(Authentication authentication, Model model)
    {
        //  Get Session Username
        model.addAttribute(
                "username",
                authentication.getName()
        );

        //  Breadcrumbs

        model.addAttribute("pageTitle", "System Settings");

        return "Dashboard/User/system-settings";
    }

    @GetMapping("/activity-log")
    public String UserActivityLog(Authentication authentication, Model model)
    {
        //  Get Session Username
        model.addAttribute(
                "username",
                authentication.getName()
        );

        //  Breadcrumbs

        model.addAttribute("pageTitle", "Activity Log");

        return "Dashboard/User/activity-log";
    }

    @GetMapping("/notifications")
    public String UserNotifications(Authentication authentication, Model model)
    {
        //  Get Session Username
        model.addAttribute(
                "username",
                authentication.getName()
        );

        //  Breadcrumbs

        model.addAttribute("pageTitle", "Notifications");

        return "Dashboard/User/notifications";
    }

    @GetMapping("/messages")
    public String UserMessages(Authentication authentication, Model model)
    {
        //  Get Session Username
        model.addAttribute(
                "username",
                authentication.getName()
        );

        //  Breadcrumbs

        model.addAttribute("pageTitle", "Messages");

        return "Dashboard/User/messages";
    }

}