package com.manage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    private Long totalEmployees;
    private Long totalDepartments;
    private Double averageSalary;

}