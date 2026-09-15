package com.manage.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.manage.security.CustomeUserDetailsService;
import com.manage.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomeUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("========== JWT FILTER ==========");
        System.out.println("REQUEST: " + request.getMethod() + " " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        System.out.println("AUTH HEADER EXISTS: " + (authHeader != null));

        if (authHeader == null) {
            System.out.println("AUTH HEADER = NULL");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("AUTH HEADER STARTS BEARER: "
                + authHeader.startsWith("Bearer "));

        if (!authHeader.startsWith("Bearer ")) {
            System.out.println("INVALID AUTH HEADER");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        String email;

        try {
            email = jwtService.extractUsername(token);

            System.out.println("JWT EMAIL: " + email);

        } catch (Exception e) {

            System.out.println("JWT ERROR: " + e.getMessage());

            filterChain.doFilter(request, response);
            return;
        }

        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            try {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                System.out.println(
                        "USER AUTHORITIES: "
                        + userDetails.getAuthorities()
                );

                if (jwtService.validateToken(
                        token,
                        userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "AUTHENTICATION SUCCESS: "
                            + SecurityContextHolder
                                    .getContext()
                                    .getAuthentication()
                    );

                } else {
                    System.out.println("JWT VALIDATION FAILED");
                }

            } catch (Exception e) {

                System.out.println(
                        "USER/JWT PROCESSING ERROR: "
                        + e.getMessage()
                );

                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }}