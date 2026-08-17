package com.manage.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.manage.model.Employee;

@Service
public class PdfService {

    public ByteArrayInputStream generatePdf(List<Employee> employees) {

        Document document = new Document();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);

            Paragraph title =
                    new Paragraph("Employee Management System", titleFont);

            title.setAlignment(Paragraph.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);

            table.setWidthPercentage(100);

            table.addCell("ID");
            table.addCell("First Name");
            table.addCell("Last Name");
            table.addCell("Email");
            table.addCell("Department");
            table.addCell("Salary");

            for (Employee emp : employees) {

                table.addCell(String.valueOf(emp.getId()));
                table.addCell(emp.getFirstName());
                table.addCell(emp.getLastName());
                table.addCell(emp.getEmail());
                table.addCell(emp.getDepartment());
                table.addCell(String.valueOf(emp.getSalary()));

            }

            document.add(table);

            document.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return new ByteArrayInputStream(out.toByteArray());

    }

}