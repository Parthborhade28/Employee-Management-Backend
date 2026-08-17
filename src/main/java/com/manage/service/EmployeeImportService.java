package com.manage.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.manage.model.Employee;
import com.manage.repo.EmployeeRepo;

@Service
public class EmployeeImportService {

    @Autowired
    private EmployeeRepo employeeRepo;

    public String importEmployees(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        List<Employee> employees = new ArrayList<>();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream())
        );

        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {

            // Skip CSV header
            if (firstLine) {
                firstLine = false;
                continue;
            }

            String[] data = line.split(",");

            if (data.length < 7) {
                continue;
            }

            String email = data[2].trim();

            // Skip duplicate email
            if (employeeRepo.existsByEmail(email)) {
                continue;
            }

            Employee employee = new Employee();

            employee.setFirstName(data[0].trim());
            employee.setLastName(data[1].trim());
            employee.setEmail(email);
            employee.setPhone(data[3].trim());
            employee.setDepartment(data[4].trim());
            employee.setSalary(Double.parseDouble(data[5].trim()));
            employee.setJoiningDate(LocalDate.parse(data[6].trim()));

            employees.add(employee);
        }

        employeeRepo.saveAll(employees);

        return employees.size() + " employees imported successfully";
    }
}