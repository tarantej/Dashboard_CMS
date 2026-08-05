package com.dashboard.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler
{
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String registrationId =
                ((OAuth2AuthenticationToken) authentication)
                        .getAuthorizedClientRegistrationId();

        System.out.println(registrationId);

        System.out.println(registrationId.toUpperCase() + " OAuth Login Successful");

//        System.out.println("==========================================");
//        System.out.println("Google OAuth Login Successful");
//        System.out.println("==========================================");
//
//        System.out.println("Name        : " + oauthUser.getAttribute("name"));
//        System.out.println("First Name  : " + oauthUser.getAttribute("given_name"));
//        System.out.println("Last Name   : " + oauthUser.getAttribute("family_name"));
//        System.out.println("Email       : " + oauthUser.getAttribute("email"));
//        System.out.println("Picture URL : " + oauthUser.getAttribute("picture"));
//        System.out.println("Google ID   : " + oauthUser.getAttribute("sub"));
//
//        System.out.println("------------------------------------------");
//        System.out.println("All Attributes");
//        System.out.println("------------------------------------------");

        oauthUser.getAttributes().forEach((key, value) ->
                System.out.println(key + " : " + value));
//
//        System.out.println("==========================================");

        // Temporary redirect
        response.sendRedirect("/dashboard");
    }
}