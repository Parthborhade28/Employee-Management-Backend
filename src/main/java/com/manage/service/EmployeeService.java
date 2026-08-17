package com.manage.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.manage.dto.DashboardChartDTO;
import com.manage.dto.DashboardDTO;
import com.manage.dto.EmployeeRequestDTO;
import com.manage.dto.EmployeeResponseDTO;
import com.manage.dto.SalaryChartDTO;
import com.manage.dto.UpdateEmailDTO;
import com.manage.dto.UpdateSalaryDTO;
import com.manage.exception.EmployeeNotFoundException;
import com.manage.model.Employee;
import com.manage.model.User;
import com.manage.repo.EmployeeRepo;
import com.manage.repo.UserRepo;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo emprepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private PdfService pdfService;


    // =====================================================
    // ADD EMPLOYEE
    // =====================================================

    public EmployeeResponseDTO save(EmployeeRequestDTO dto)
            throws IOException {

        if (emprepo.existsByEmail(dto.getEmail())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }

        Employee emp = new Employee();

        emp.setFirstName(dto.getFirstName());
        emp.setLastName(dto.getLastName());
        emp.setEmail(dto.getEmail());
        emp.setPhone(dto.getPhone());
        emp.setDepartment(dto.getDepartment());
        emp.setSalary(dto.getSalary());
        emp.setJoiningDate(dto.getJoiningDate());

        // Upload profile image
        if (dto.getProfileImage() != null
                && !dto.getProfileImage().isEmpty()) {

            String fileName =
                    fileService.uploadFile(
                            dto.getProfileImage()
                    );

            emp.setProfileImage(fileName);
        }

        Employee savedEmployee =
                emprepo.save(emp);


        // =================================================
        // SYNC REGISTERED USER
        // =================================================

        syncUserWithEmployee(savedEmployee);


        return convertToResponse(savedEmployee);
    }


    // =====================================================
    // SYNC USER DATA
    // =====================================================

    private void syncUserWithEmployee(
            Employee employee) {

        userRepo.findByEmail(
                employee.getEmail()
        ).ifPresent(user -> {

            user.setDepartment(
                    employee.getDepartment()
            );

            user.setSalary(
                    employee.getSalary()
            );

            user.setJoiningDate(
                    employee.getJoiningDate()
            );

            userRepo.save(user);
        });
    }


    // =====================================================
    // GET ALL EMPLOYEES
    // =====================================================

    public List<EmployeeResponseDTO> getAll() {

        return emprepo.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =====================================================
    // GET EMPLOYEE BY ID
    // =====================================================

    public EmployeeResponseDTO getEmployeeById(
            Long id) {

        Employee emp =
                emprepo.findById(id)
                        .orElseThrow(
                                () -> new EmployeeNotFoundException(
                                        "Employee Not Found"
                                )
                        );

        return convertToResponse(emp);
    }


    // =====================================================
    // UPDATE EMPLOYEE
    // =====================================================

    public EmployeeResponseDTO updateEmployee(
            Long id,
            EmployeeRequestDTO dto)
            throws IOException {

        Employee emp =
                emprepo.findById(id)
                        .orElseThrow(
                                () -> new EmployeeNotFoundException(
                                        "Employee Not Found"
                                )
                        );

        String oldEmail = emp.getEmail();

        emp.setFirstName(
                dto.getFirstName()
        );

        emp.setLastName(
                dto.getLastName()
        );

        emp.setEmail(
                dto.getEmail()
        );

        emp.setPhone(
                dto.getPhone()
        );

        emp.setDepartment(
                dto.getDepartment()
        );

        emp.setSalary(
                dto.getSalary()
        );

        emp.setJoiningDate(
                dto.getJoiningDate()
        );


        // Upload new image
        if (dto.getProfileImage() != null
                && !dto.getProfileImage().isEmpty()) {

            String fileName =
                    fileService.uploadFile(
                            dto.getProfileImage()
                    );

            emp.setProfileImage(fileName);
        }


        Employee updatedEmployee =
                emprepo.save(emp);


        // =================================================
        // UPDATE REGISTERED USER
        // =================================================

        syncUserAfterUpdate(
                oldEmail,
                updatedEmployee
        );


        return convertToResponse(
                updatedEmployee
        );
    }


    // =====================================================
    // SYNC USER AFTER EMPLOYEE UPDATE
    // =====================================================

    private void syncUserAfterUpdate(
            String oldEmail,
            Employee employee) {

        /*
         * First try the employee's NEW email.
         */

        User user =
                userRepo.findByEmail(
                        employee.getEmail()
                ).orElse(null);


        /*
         * If email was changed and the user still has
         * the old email, find the old account.
         */

        if (user == null
                && oldEmail != null
                && !oldEmail.equals(
                        employee.getEmail())) {

            user =
                    userRepo.findByEmail(
                            oldEmail
                    ).orElse(null);
        }


        if (user != null) {

            /*
             * Keep the registered account email
             * synchronized with employee email.
             */

            user.setEmail(
                    employee.getEmail()
            );

            user.setDepartment(
                    employee.getDepartment()
            );

            user.setSalary(
                    employee.getSalary()
            );

            user.setJoiningDate(
                    employee.getJoiningDate()
            );

            userRepo.save(user);
        }
    }


    // =====================================================
    // DELETE EMPLOYEE
    // =====================================================

    public String deleteEmployee(Long id) {

        Employee emp =
                emprepo.findById(id)
                        .orElseThrow(
                                () -> new EmployeeNotFoundException(
                                        "Employee not found"
                                )
                        );

        /*
         * We intentionally DO NOT delete the User account.
         *
         * Example:
         *
         * Employee deleted
         * User account remains available.
         */

        emprepo.delete(emp);

        return "Employee Deleted Successfully";
    }


    // =====================================================
    // SEARCH EMPLOYEE
    // =====================================================

    public Page<EmployeeResponseDTO> searchEmployee(
            String name,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        Page<Employee> employees =
                emprepo.findByFirstNameContainingIgnoreCase(
                        name,
                        pageable
                );

        return employees.map(
                this::convertToResponse
        );
    }


    // =====================================================
    // PAGINATION
    // =====================================================

    public Page<EmployeeResponseDTO> getEmployees(
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return emprepo
                .findAll(pageable)
                .map(this::convertToResponse);
    }


    // =====================================================
    // SORT EMPLOYEES
    // =====================================================

    public List<EmployeeResponseDTO> sortEmployee(
            String field) {

        return emprepo
                .findAll(
                        Sort.by(field)
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =====================================================
    // CONVERT TO RESPONSE
    // =====================================================

    private EmployeeResponseDTO convertToResponse(
            Employee employee) {

        EmployeeResponseDTO response =
                modelMapper.map(
                        employee,
                        EmployeeResponseDTO.class
                );

        if (employee.getProfileImage() != null) {

            response.setProfileImage(
                    "http://localhost:8080/employees/image/"
                            + employee.getProfileImage()
            );
        }

        return response;
    }


    // =====================================================
    // UPDATE SALARY
    // =====================================================

    public EmployeeResponseDTO updatesalary(
            UpdateSalaryDTO dto,
            Long id) {

        Employee emp =
                emprepo.findById(id)
                        .orElseThrow(
                                () -> new EmployeeNotFoundException(
                                        "Employee not found " + id
                                )
                        );

        emp.setSalary(
                dto.getSalary()
        );

        Employee updated =
                emprepo.save(emp);


        // Sync salary with registered user
        syncUserWithEmployee(updated);


        return modelMapper.map(
                updated,
                EmployeeResponseDTO.class
        );
    }


    // =====================================================
    // UPDATE EMAIL
    // =====================================================

    public EmployeeResponseDTO updateEmail(
            UpdateEmailDTO dto,
            Long id) {

        Employee emp =
                emprepo.findById(id)
                        .orElseThrow(
                                () -> new EmployeeNotFoundException(
                                        "Emp not found"
                                )
                        );

        if (!emp.getEmail().equals(
                dto.getEmail())
                && emprepo.existsByEmail(
                        dto.getEmail())) {

            throw new IllegalArgumentException(
                    "Email already present"
            );
        }

        String oldEmail =
                emp.getEmail();

        emp.setEmail(
                dto.getEmail()
        );

        Employee updated =
                emprepo.save(emp);


        // Sync registered user's email
        syncUserAfterUpdate(
                oldEmail,
                updated
        );


        return modelMapper.map(
                updated,
                EmployeeResponseDTO.class
        );
    }


    // =====================================================
    // DASHBOARD
    // =====================================================

    public DashboardDTO getDashboardData() {

        return new DashboardDTO(
                emprepo.totalEmployees(),
                emprepo.totalDepartments(),
                emprepo.averageSalary()
        );
    }


    // =====================================================
    // RECENT EMPLOYEES
    // =====================================================

    public List<EmployeeResponseDTO>
    getRecentEmployees() {

        return emprepo
                .findTop5ByOrderByIdDesc()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // =====================================================
    // DEPARTMENT CHART
    // =====================================================

    public List<DashboardChartDTO>
    getDepartmentChart() {

        return emprepo.employeeDepartmentChart();
    }


    // =====================================================
    // EXPORT EXCEL
    // =====================================================

    public ByteArrayInputStream exportEmployees()
            throws IOException {

        List<Employee> employees =
                emprepo.findAll();

        return excelService.exportEmployees(
                employees
        );
    }


    // =====================================================
    // EXPORT PDF
    // =====================================================

    public ByteArrayInputStream exportPdf() {

        List<Employee> employees =
                emprepo.findAll();

        return pdfService.generatePdf(
                employees
        );
    }


    // =====================================================
    // SALARY CHART
    // =====================================================

    public List<SalaryChartDTO> getSalaryChart() {

        return emprepo.getSalaryChart();
    }
}