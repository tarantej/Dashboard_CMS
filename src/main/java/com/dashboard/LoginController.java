package com.dashboard;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

import com.dashboard.service.CustomUserDetailsService;
import com.dashboard.service.ImageDownloadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import com.dashboard.service.MailService;

@Controller

public class LoginController
{
    //    Login

    @GetMapping("/login")
    public String loginPage()
    {

        return"Login/login";
    }

    // Logout

    @GetMapping("/logout")
    public String logout(HttpSession session)
    {
        session.invalidate();

        return "redirect:/login";
    }

    // OAuth

    @GetMapping("/oauth-signup")
    public String OAuthSignupPage(Model model, Authentication authentication)
    {
        if (authentication == null) {
            return "redirect:/login";
        }

        if (!(authentication.getPrincipal() instanceof OAuth2User oauthUser)) {
            return "redirect:/login";
        }

        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken) authentication;

        String provider =
                token.getAuthorizedClientRegistrationId();

        String providerLogo = "";

        String displayName = "";
        String firstName = "";
        String lastName = "";
        String email = "";
        String phoneNumber = "";
        String profilePicture = "";
        String userBio = "";
        String city = "";
        String country = "";

        if (provider.equals("google"))
        {
            displayName = oauthUser.getAttribute("name");
            email = oauthUser.getAttribute("email");
            profilePicture = oauthUser.getAttribute("picture");

            provider = "Google";
            providerLogo = "/img/Google.svg";
        }

        else if (provider.equals("github"))
        {
            displayName = oauthUser.getAttribute("name");

            if (displayName == null || displayName.isBlank())
            {
                displayName = oauthUser.getAttribute("login");
            }

            email = oauthUser.getAttribute("email");
            profilePicture = oauthUser.getAttribute("avatar_url");

            provider = "GitHub";
            providerLogo = "/img/GitHub.svg";
        }

        model.addAttribute("displayName", displayName);
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("email", email);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("userBio", userBio);
        model.addAttribute("city", city);
        model.addAttribute("country", country);
        model.addAttribute("picture", profilePicture);

        model.addAttribute("provider", provider);
        model.addAttribute("providerLogo", providerLogo);

        return "Login/OAuth/oauth-signup";
    }

    @Autowired
    private ImageDownloadService imageDownloadService;

    @PostMapping("/oauth-signup")
    public String PostOAuthSignup(Model model,
                                  @RequestParam String username,
                                  @RequestParam String city,
                                  @RequestParam String country,
                                  @RequestParam(required = false) String phoneNumber,
                                  @RequestParam(required = false) String userBio,
                                  @RequestParam String password,
                                  @RequestParam String confirmPassword,
                                  Authentication authentication)
    {
        if (authentication == null) {
            return "redirect:/login";
        }

        if (!(authentication.getPrincipal() instanceof OAuth2User oauthUser)) {
            return "redirect:/login";
        }

        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken) authentication;

        String provider =
                token.getAuthorizedClientRegistrationId();

        String firstName = "";
        String lastName = "";
        String email = "";
        String profilePicture = "";

        String oauthId = "";
        String oauthProvider = "";
        String oauthPicture = "";


        //  Check the OAuth Provider

        if (provider.equals("google"))
        {
            email = oauthUser.getAttribute("email");
            firstName = oauthUser.getAttribute("given_name");
            lastName = oauthUser.getAttribute("family_name");
            profilePicture = oauthUser.getAttribute("picture");

            oauthProvider = "GOOGLE";
            oauthId = oauthUser.getAttribute("sub");
            oauthPicture = profilePicture;
        }
        else if (provider.equals("github"))
        {
            email = oauthUser.getAttribute("email");
            profilePicture = oauthUser.getAttribute("avatar_url");

            String fullName = oauthUser.getAttribute("name");

            oauthProvider = "GITHUB";
            Object githubId = oauthUser.getAttribute("id");
            oauthId = githubId.toString();
//            oauthId = String.valueOf(oauthUser.getAttribute("id"));
            oauthPicture = profilePicture;

            if (fullName != null && !fullName.isBlank())
            {
                String[] parts = fullName.split(" ", 2);

                firstName = parts[0];

                if (parts.length > 1)
                {
                    lastName = parts[1];
                }
            }
            else
            {
                String login = oauthUser.getAttribute("login");
                firstName = login;
            }
        }

        //  Check if user already exists in the Dashboard

        Optional<DashboardUsers> existingUser =
                userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return "redirect:/dashboard";
        }

        //  User Dashboard Signup

        DashboardUsers user = new DashboardUsers();

        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setUserBio(userBio);
        user.setUserCity(city);
        user.setUserCountry(country);
        user.setOauthProvider(oauthProvider);
        user.setOauthId(oauthId);
        user.setOauthPicture(oauthPicture);

        String localProfilePicture = null;

        if (oauthPicture != null && !oauthPicture.isBlank())
        {
            localProfilePicture =
                    imageDownloadService.downloadProfilePicture(
                            oauthPicture,
                            username
                    );
        }

        user.setProfilePicture(localProfilePicture);

        user.setUserRole("EMPLOYEE");


        if (password == null || confirmPassword == null ||
                !password.equals(confirmPassword))
        {
            model.addAttribute(
                    "error",
                    "Passwords do not match.");

            return "Login/OAuth/oauth-signup";
        }

        user.setPassword(
                passwordEncoder.encode(password));

        Timestamp now =
                new Timestamp(System.currentTimeMillis());

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);
        return "redirect:/oauth-success";
    }

    @GetMapping("/oauth-success")
    public String OAuthSuccessPage(Model model)
    {
        //  Breadcrumbs

        model.addAttribute("pageTitle", "Success");
        return "Login/OAuth/oauth-success";
    }

    // Forgot Password

    @GetMapping("/forgot-password")
    public String forgotPassword()
    {
        return "Login/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            Model model)

    {
        Optional<DashboardUsers> user= userRepository.findByEmail(email);
        if(user.isPresent())
        {
            System.out.println("User found: " + user.get().getUsername());
            String token = UUID.randomUUID().toString();

            Timestamp expiry =
                    new Timestamp(System.currentTimeMillis()
                            + (15 * 60 * 1000));

            user.get().setResetToken(token);
            user.get().setResetTokenExpiry(expiry);

            userRepository.save(user.get());

            String resetLink = "http://localhost:8800/reset-password?token=" + token;

            mailService.sendResetEmail(email, resetLink);

            System.out.println(resetLink);
        }
        else
        {
            System.out.println("No User exists with email: " + email);
        }

        model.addAttribute("email", email);

        return "Login/success";

    }

    @Autowired
    private MailService mailService;



    // Reset Password

    @GetMapping("/reset-password")
    public String resetPassword
            (
            @RequestParam String token,
            Model model
    )
    {
        Optional<DashboardUsers> user =
                userRepository.findByResetToken(token);



        if(user.isEmpty())
        {
            model.addAttribute(
                    "error",
                    "Invalid password reset link.");

            return "Login/forgot-password";
        }

        Timestamp now =
                new Timestamp(System.currentTimeMillis());

        if(user.get().getResetTokenExpiry().before(now))
        {
            model.addAttribute(
                    "error",
                    "Password reset link has expired.");

            return "Login/forgot-password";
        }

                DashboardUsers resetUser = user.get();

        model.addAttribute("first_name", resetUser.getFirstName());
        model.addAttribute("last_name", resetUser.getLastName());
        model.addAttribute("token", token);


        return "Login/reset-password";
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String token,
            @RequestParam String NewPassword,
            @RequestParam String confirmPassword,
            Model model
    )
    {
        Optional<DashboardUsers> user =
                userRepository.findByResetToken(token);

        if(user.isEmpty())
        {
            model.addAttribute(
                    "error",
                    "Invalid password reset link.");

            return "Login/forgot-password";
        }

        Timestamp now =
                new Timestamp(System.currentTimeMillis());

        if(user.get().getResetTokenExpiry().before(now))
        {
            model.addAttribute(
                    "error",
                    "Password reset link has expired.");

            return "Login/forgot-password";
        }

        if(!NewPassword.equals(confirmPassword))
        {
            model.addAttribute(
                    "error",
                    "Passwords do not match.");

            return "Login/reset-password";
        }

        DashboardUsers resetUser = user.get();

        resetUser.setPassword(
                passwordEncoder.encode(NewPassword)
        );

        resetUser.setResetToken(null);

        resetUser.setResetTokenExpiry(null);

        userRepository.save(resetUser);

        return "Login/reset-success";
    }

    //    Checking if User exists in database

//    @GetMapping("/test-user")
//    @ResponseBody
//    public String testUser() {
//
//        DashboardUsers user =
//                userRepository.findByUsername("SupAdmin")
//                        .orElse(null);
//
//        if (user == null) {
//            return "User not found";
//        }
//
//        return user.getUsername();
//    }
}