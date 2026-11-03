package com.dashboard.controller;

import com.dashboard.DashboardUsers;
import com.dashboard.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAttributes
{
    private final UserRepository userRepository;

    public GlobalModelAttributes(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addUserAttributes(Model model) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return;
        }

        DashboardUsers user;

        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {

            String email = oauthUser.getAttribute("email");

            user = userRepository.findByEmail(email).orElse(null);

        } else {

            user = userRepository.findByUsername(authentication.getName())
                    .orElse(null);
        }

        if (user != null) {
            model.addAttribute("first_name", user.getFirstName());
            model.addAttribute("profile_picture", user.getProfilePicture());
            model.addAttribute("oauth_provider", user.getOauthProvider());
            model.addAttribute("oauth_picture", user.getOauthPicture());
        }
    }
}