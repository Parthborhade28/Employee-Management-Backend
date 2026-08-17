package com.manage.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manage.dto.DashboardChartDTO;
import com.manage.dto.DashboardDTO;
import com.manage.dto.EmployeeRequestDTO;
import com.manage.dto.EmployeeResponseDTO;
import com.manage.dto.SalaryChartDTO;
import com.manage.dto.UpdateEmailDTO;
import com.manage.dto.UpdateSalaryDTO;
import com.manage.service.EmployeeImportService;
import com.manage.service.EmployeeService;
import com.manage.service.FileService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @Autowired
    private FileService fileService;

    @Autowired
    private EmployeeImportService employeeImportService;


    // =====================================================
    // ADMIN - GET ALL EMPLOYEES
    // =====================================================

    @Operation(summary = "Get all employees")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeResponseDTO>> getAll() {

        return ResponseEntity.ok(
                service.getAll()
        );
    }


    // =====================================================
    // ADMIN - GET EMPLOYEE BY ID
    // =====================================================

    @Operation(summary = "Get employee by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getEmployeeById(id)
        );
    }


    // =====================================================
    // ADMIN - UPDATE EMPLOYEE
    // =====================================================

    @PutMapping(
            value = "/{id}",
            consumes = "multipart/form-data"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute EmployeeRequestDTO dto)
            throws IOException {

        return ResponseEntity.ok(
                service.updateEmployee(id, dto)
        );
    }


    // =====================================================
    // ADMIN - DELETE EMPLOYEE
    // =====================================================

    @Operation(summary = "Delete employee")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.deleteEmployee(id)
        );
    }


    // =====================================================
    // ADMIN - SEARCH EMPLOYEES
    // =====================================================

    @Operation(summary = "Search employees")
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<
            org.springframework.data.domain.Page<EmployeeResponseDTO>
    > searchEmployee(

            @RequestParam String name,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "5"
            )
            int size) {

        return ResponseEntity.ok(
                service.searchEmployee(
                        name,
                        page,
                        size
                )
        );
    }


    // =====================================================
    // ADMIN - PAGINATION
    // =====================================================

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<
            org.springframework.data.domain.Page<EmployeeResponseDTO>
    > getEmployees(

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "5"
            )
            int size) {

        return ResponseEntity.ok(
                service.getEmployees(
                        page,
                        size
                )
        );
    }


    // =====================================================
    // ADMIN - SORT
    // =====================================================

    @Operation(summary = "Sort employees")
    @GetMapping("/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeResponseDTO>> sortEmployee(
            @RequestParam String field) {

        return ResponseEntity.ok(
                service.sortEmployee(field)
        );
    }


    // =====================================================
    // ADMIN - ADD EMPLOYEE
    // =====================================================

    @PostMapping(
            consumes = "multipart/form-data"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> save(
            @Valid @ModelAttribute EmployeeRequestDTO dto)
            throws IOException {

        return ResponseEntity.ok(
                service.save(dto)
        );
    }


    // =====================================================
    // IMAGE - ADMIN/USER
    // =====================================================

    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String fileName)
            throws IOException {

        Resource resource =
                fileService.downloadFile(fileName);

        String contentType =
                Files.probeContentType(
                        Paths.get(resource.getURI())
                );

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(contentType)
                )
                .body(resource);
    }


    // =====================================================
    // ADMIN - UPDATE SALARY
    // =====================================================

    @PutMapping("/{id}/salary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> updateSalary(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSalaryDTO dto) {

        EmployeeResponseDTO response =
                service.updatesalary(
                        dto,
                        id
                );

        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // ADMIN - UPDATE EMAIL
    // =====================================================

    @PutMapping("/{id}/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponseDTO> updateEmail(
            @RequestBody @Valid UpdateEmailDTO dto,
            @PathVariable Long id) {

        EmployeeResponseDTO response =
                service.updateEmail(
                        dto,
                        id
                );

        return ResponseEntity.ok(
                response
        );
    }


    // =====================================================
    // ADMIN - DASHBOARD
    // =====================================================

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDTO> dashboard() {

        return ResponseEntity.ok(
                service.getDashboardData()
        );
    }


    // =====================================================
    // ADMIN - RECENT EMPLOYEES
    // =====================================================

    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeResponseDTO>>
    getRecentEmployees() {

        return ResponseEntity.ok(
                service.getRecentEmployees()
        );
    }


    // =====================================================
    // ADMIN - EXCEL EXPORT
    // =====================================================

    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource>
    exportEmployees()
            throws IOException {

        ByteArrayInputStream excel =
                service.exportEmployees();

        HttpHeaders headers =
                new HttpHeaders();

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=employees.xlsx"
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(
                        new InputStreamResource(
                                excel
                        )
                );
    }


    // =====================================================
    // ADMIN - PDF EXPORT
    // =====================================================

    @GetMapping("/export/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource>
    exportPdf() {

        ByteArrayInputStream pdf =
                service.exportPdf();

        HttpHeaders headers =
                new HttpHeaders();

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=employees.pdf"
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(
                        new InputStreamResource(
                                pdf
                        )
                );
    }


    // =====================================================
    // ADMIN - DEPARTMENT CHART
    // =====================================================

    @GetMapping("/dashboard/chart")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DashboardChartDTO>>
    getDepartmentChart() {

        return ResponseEntity.ok(
                service.getDepartmentChart()
        );
    }


    // =====================================================
    // ADMIN - SALARY CHART
    // =====================================================

    @GetMapping("/dashboard/salary-chart")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SalaryChartDTO>>
    getSalaryChart() {

        return ResponseEntity.ok(
                service.getSalaryChart()
        );
    }


    // =====================================================
    // ADMIN - IMPORT CSV
    // =====================================================

    @PostMapping(
            value = "/import",
            consumes = "multipart/form-data"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> importEmployees(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        return ResponseEntity.ok(
                employeeImportService
                        .importEmployees(file)
        );
    }
}