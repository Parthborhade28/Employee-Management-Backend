package com.manage.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.manage.model.Employee;

@Service
public class ExcelService {

    public ByteArrayInputStream exportEmployees(List<Employee> employees) throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Employees");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("First Name");
        header.createCell(2).setCellValue("Last Name");
        header.createCell(3).setCellValue("Email");
        header.createCell(4).setCellValue("Phone");
        header.createCell(5).setCellValue("Department");
        header.createCell(6).setCellValue("Salary");
        header.createCell(7).setCellValue("Joining Date");

        int rowNum = 1;

        for (Employee emp : employees) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(emp.getId());

            row.createCell(1).setCellValue(emp.getFirstName());

            row.createCell(2).setCellValue(emp.getLastName());

            row.createCell(3).setCellValue(emp.getEmail());

            row.createCell(4).setCellValue(emp.getPhone());

            row.createCell(5).setCellValue(emp.getDepartment());

            row.createCell(6).setCellValue(emp.getSalary());

            row.createCell(7).setCellValue(emp.getJoiningDate().toString());

        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        workbook.write(out);

        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());

    }

}