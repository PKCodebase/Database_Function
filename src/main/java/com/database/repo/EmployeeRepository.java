package com.database.repo;

import com.database.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query(value = "SELECT * FROM get_employee()", nativeQuery = true)
    List<Object[]> getAllEmployees();

    @Query(value = "SELECT * FROM add_employee(:empName, :designation, :department, :mobile)",
            nativeQuery = true)
    String addEmployee(
            @Param("empName")      String empName,
            @Param("designation")  String designation,
            @Param("department")   String department,
            @Param("mobile")       String mobile
    );

    @Query(value = "SELECT * FROM update_employee(:id, :empName, :designation, :department, :mobile)",
            nativeQuery = true)
    String updateEmployee(
            @Param("id")          Long id,
            @Param("empName")     String empName,
            @Param("designation") String designation,
            @Param("department")  String department,
            @Param("mobile")      String mobile
    );
}