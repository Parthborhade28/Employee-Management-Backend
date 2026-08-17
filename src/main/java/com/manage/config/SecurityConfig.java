package com.manage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.manage.filter.JwtFilter;
import com.manage.security.CustomeUserDetailsService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomeUserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;


    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        System.out.println(
                "Custom SecurityConfig Loaded"
        );

        http
            .csrf(csrf -> csrf.disable())

            .cors(Customizer.withDefaults())

            .authorizeHttpRequests(auth -> auth

                // ==========================================
                // PUBLIC AUTH ENDPOINTS
                // ==========================================

                .requestMatchers(
                    "/auth/login",
                    "/auth/register",
                    "/auth/forgot-password",
                    "/auth/verify-otp",
                    "/auth/reset-password",
                    "/auth/test-email"
                ).permitAll()


                // ==========================================
                // PUBLIC PROFILE IMAGES
                // ==========================================

                .requestMatchers(
                    "/employees/image/**"
                ).permitAll()


                // ==========================================
                // PROFILE
                // ADMIN + USER
                // ==========================================

                .requestMatchers(
                    "/auth/profile"
                ).authenticated()


                // ==========================================
                // EVERYTHING ELSE UNDER EMPLOYEES
                // ADMIN ONLY
                // ==========================================

                .requestMatchers(
                    "/employees/**"
                ).hasRole("ADMIN")


                // ==========================================
                // EVERYTHING ELSE
                // ==========================================

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }


    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    // =====================================================
    // AUTHENTICATION PROVIDER
    // =====================================================

    @Bean
    AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                userDetailsService
        );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        return provider;
    }


    // =====================================================
    // AUTHENTICATION MANAGER
    // =====================================================

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }
}