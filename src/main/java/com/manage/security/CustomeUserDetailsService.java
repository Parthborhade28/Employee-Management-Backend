package com.manage.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.manage.model.User;
import com.manage.repo.UserRepo;

@Service
public class CustomeUserDetailsService
        implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {

        User user = repo.findByEmail(email)
                .orElseThrow(
                    () -> new UsernameNotFoundException(
                        "User not found"
                    )
                );

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())

                // USER → ROLE_USER
                // ADMIN → ROLE_ADMIN
                .roles(user.getRole())

                .build();
    }
}