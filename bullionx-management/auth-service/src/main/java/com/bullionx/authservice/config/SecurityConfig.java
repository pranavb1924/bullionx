package com.bullionx.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for API endpoints
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll() // Allow registration/login
                        .requestMatchers("/api/users/register").permitAll() // Allow user registration
                        .requestMatchers("/api/users/login").permitAll() // Allow user login
                        .requestMatchers("/h2-console/**").permitAll() // If using H2 for testing
                        .anyRequest().authenticated() // Require auth for everything else
                )
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // For REST API

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
