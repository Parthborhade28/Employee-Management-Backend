package com.manage.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manage.dto.DashboardChartDTO;
import com.manage.dto.SalaryChartDTO;
import com.manage.model.Employee;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, Long> {

    // =====================================================
    // FIND EMPLOYEE BY EMAIL
    // =====================================================

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);


    // =====================================================
    // USER - FIND ONLY THEIR OWN EMPLOYEE
    // =====================================================

    Optional<Employee> findByEmailIgnoreCase(String email);


    // =====================================================
    // SEARCH
    // =====================================================

    Page<Employee> findByFirstNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );


    // =====================================================
    // RECENT EMPLOYEES
    // =====================================================

    List<Employee> findTop5ByOrderByIdDesc();


    // =====================================================
    // DEPARTMENT CHART
    // =====================================================

    @Query("""
            SELECT new com.manage.dto.DashboardChartDTO(
                e.department,
                COUNT(e)
            )
            FROM Employee e
            GROUP BY e.department
            """)
    List<DashboardChartDTO> employeeDepartmentChart();


    // =====================================================
    // TOTAL EMPLOYEES
    // =====================================================

    @Query("SELECT COUNT(e) FROM Employee e")
    Long totalEmployees();


    // =====================================================
    // TOTAL DEPARTMENTS
    // =====================================================

    @Query(
        "SELECT COUNT(DISTINCT e.department) FROM Employee e"
    )
    Long totalDepartments();


    // =====================================================
    // AVERAGE SALARY
    // =====================================================

    @Query("SELECT AVG(e.salary) FROM Employee e")
    Double averageSalary();


    // =====================================================
    // SALARY CHART
    // =====================================================

    @Query("""
            SELECT new com.manage.dto.SalaryChartDTO(
                e.department,
                AVG(e.salary)
            )
            FROM Employee e
            GROUP BY e.department
            """)
    List<SalaryChartDTO> getSalaryChart();
}