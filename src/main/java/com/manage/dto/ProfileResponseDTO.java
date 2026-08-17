package com.manage.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {

    private Long id;

    private String name;

    private String email;

    private String role;

    private String department;

    private Double salary;

    private LocalDate joiningDate;

    private String profileImage;
}