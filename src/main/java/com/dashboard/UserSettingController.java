package com.dashboard;

import java.io.IOException;
import java.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;



@Controller
public class UserSettingController
{

    @Autowired
    private UserRepository userRepository;


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
        //  Get Session Username
        model.addAttribute(
                "username",
                authentication.getName()
        );

        //  Breadcrumbs

        model.addAttribute("pageTitle", "Account Settings");

        return "Dashboard/User/account-settings";
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