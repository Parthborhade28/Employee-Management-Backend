package com.manage.dto;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDTO {

    @NotBlank(message = "First Name is required")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    private String lastName;

    @Email(message = "Invalid Email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Salary is required")
    @Positive(message = "Salary must be greater than 0")
    private Double salary;

    @NotNull(message = "Joining Date is required")
    private LocalDate joiningDate;

    private MultipartFile profileImage;

    // Getters and Setters
}