package com.manage.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.manage.dto.AuthResponse;
import com.manage.dto.ForgotPasswordRequest;
import com.manage.dto.LoginRequest;
import com.manage.dto.ProfileResponseDTO;
import com.manage.dto.RegisterRequest;
import com.manage.model.Employee;
import com.manage.model.User;
import com.manage.repo.EmployeeRepo;
import com.manage.repo.UserRepo;
import com.manage.security.JwtService;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OtpService otpService;


    // =====================================================
    // REGISTER
    // =====================================================

    public AuthResponse register(RegisterRequest dto) {

        // Check duplicate email
        if (repo.existsByEmail(dto.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        // Create User
        User user = mapper.map(
                dto,
                User.class
        );

        // Encode password
        user.setPassword(
                encoder.encode(dto.getPassword())
        );

        // Self-registered users are always USER
        user.setRole("USER");

        // Employee information will be assigned
        // when Admin creates/updates the employee
        user.setDepartment(null);
        user.setSalary(null);
        user.setJoiningDate(null);

        repo.save(user);

        return new AuthResponse(
                "User Registered Successfully"
        );
    }


    // =====================================================
    // LOGIN
    // =====================================================

    public AuthResponse login(LoginRequest dto) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        String token =
                jwtService.generateToken(
                        dto.getEmail()
                );

        return new AuthResponse(token);
    }


    // =====================================================
    // GET PROFILE
    // =====================================================
    public ProfileResponseDTO getProfile(
            String email
    ) {

        User user = repo.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User Not Found"
                        )
                );

        Employee employee =
                employeeRepo.findByEmailIgnoreCase(email)
                .orElse(null);

        if (employee != null) {

            String profileImage = null;

            if (employee.getProfileImage() != null) {

                profileImage =
                        "http://localhost:8080/employees/image/"
                        + employee.getProfileImage();
            }

            return new ProfileResponseDTO(

                    user.getId(),

                    employee.getFirstName()
                            + " "
                            + employee.getLastName(),

                    user.getEmail(),

                    user.getRole(),

                    employee.getDepartment(),

                    employee.getSalary(),

                    employee.getJoiningDate(),

                    profileImage
            );
        }

        return new ProfileResponseDTO(

                user.getId(),

                user.getName(),

                user.getEmail(),

                user.getRole(),

                null,

                null,

                null,

                null
        );
    }


    // =====================================================
    // FORGOT PASSWORD
    // =====================================================

    public String forgotPassword(
            ForgotPasswordRequest dto
    ) {

        User user = repo.findByEmail(
                dto.getEmail()
        ).orElseThrow(
                () -> new RuntimeException(
                        "User not found"
                )
        );

        otpService.generateOtp(
                user.getEmail()
        );

        return "OTP sent successfully.";
    }
}