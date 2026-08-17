package com.manage.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String department;
	private Double salary;
	private LocalDate joiningDate;
	private String profileImage;
	// Getters & Setters
}