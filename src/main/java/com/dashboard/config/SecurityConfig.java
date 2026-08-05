package com.dashboard.config;

import com.dashboard.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.dashboard.config.OAuth2LoginSuccessHandler;

@Configuration

public class SecurityConfig
{

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/profile",
                                "/system-settings",
                                "/account-settings",
                                "/messages",
                                "/notifications",
                                "/activity-log",
                                "/blank",
                                "/forgot-password",
                                "/reset-password",
                                "/oauth-signup",
                                "/oauth-success",
                                "/success",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/uploads/**",
                                "/400",
                                "/401",
                                "/403",
                                "/404",
                                "/405",
                                "/408",
                                "/409",
                                "/422",
                                "/429",
                                "/500",
                                "/502",
                                "/503",
                                "/sb-admin-templates/**"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )

                //  Google OAuth

                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//    No encrypted password

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }

//    Password using BCrypt

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }



//    @Bean
//    public UserDetailsService users() {
//        UserDetails user =
//                User.withDefaultPasswordEncoder()
//                        .username("user")
//                        .password("password")
//                        .roles("USER")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
}