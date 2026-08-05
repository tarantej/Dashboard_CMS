package com.dashboard;

import java.sql.*;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
public class DashboardController
{

    @GetMapping("/")
    public String home()
    {

        return "index";
    }


    //  Dashboard Page

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String Dashboard(Model model,
                            Authentication authentication)
    {

        DashboardUsers user;

        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {

            String email = oauthUser.getAttribute("email");

            user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            if (user == null) {
                return "redirect:/oauth-signup";
            }

        } else {

            user = userRepository
                    .findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }


        //  Greeting based on time of the day

        LocalTime now = LocalTime.now();

        String greeting;

        if (now.getHour() < 12) {
            greeting = "Good Morning";
        }
        else if (now.getHour() < 17) {
            greeting = "Good Afternoon";
        }
        else {
            greeting = "Good Evening";
        }

        model.addAttribute("greeting", greeting);

        //  Get Session Username
        model.addAttribute("first_name", user.getFirstName());

        //  Get Profile Picture

        model.addAttribute("profile_picture", user.getProfilePicture());
        model.addAttribute("oauth_provider", user.getOauthProvider());
        model.addAttribute("oauth_picture", user.getOauthPicture());

        return "Dashboard/dashboard";
    }


    @GetMapping("/blank")
    public String BlankPage(Model model, Authentication authentication)
    {

        DashboardUsers user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));


        //  Get Session Username
        model.addAttribute(
                "first_name",
                user.getFirstName()
        );

        //  Breadcrumbs

        model.addAttribute("pageTitle", "Blank Page");
        
        return "Dashboard/blank";
    }


}