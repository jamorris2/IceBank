package com.icebank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Handles Authorization:
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/signup",
                                "/verify",
                                "/forgot-password",
                                "/changePassword",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/h2-console/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Handles Authentication:
                .formLogin(login -> login
                        .loginPage("/login")  // sets the default page the user sees when they try to access an endpoint when authentication is required and the user isn't authorized
                        .loginProcessingUrl("/processLogin")  // Sets the /processLogin url as the endpoint (to handle Post requests) that gets hit when a user tries to login
                        .usernameParameter("emailAddress")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureHandler(customAuthenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

        return http.build();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            String status = "error";

            if (exception instanceof DisabledException) {
                status = "account-unverified";
            } else if (exception instanceof BadCredentialsException) {
                status = "bad-credentials";
            }

            String email = request.getParameter("emailAddress");
            response.sendRedirect("/login?status=" + status + "&email=" + email);
        };
    }
}
