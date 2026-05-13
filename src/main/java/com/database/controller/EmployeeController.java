package com.database.controller;

import com.database.dto.EmployeeDTO;
import com.database.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.version.v1}/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        log.info("Request received → GET /all employees");
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        log.info("Total employees fetched → {}", employees.size());
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> addEmployees(@RequestBody EmployeeDTO employeeDTO){
        log.info("Request received → POST /add employee");
        String employees = employeeService.addEmployee(employeeDTO);
        log.info("Employee added successfully → {}", employees);
        return ResponseEntity.status(HttpStatus.CREATED).body(employees);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        log.info("Request received → GET /employee/{}", id);
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        log.info("Employee fetched successfully → {}", employee.getEmpName());
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id,@RequestBody EmployeeDTO employeeDTO) {
        log.info("Request received → PUT /update/{}", id);
        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        log.info("Employee updated successfully → {}", updated.getEmpName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Long id) {
        log.info("Request received → DELETE /delete/{}", id);
        employeeService.deleteEmployeeById(id);
        log.info("Employee deleted successfully → id: {}", id);
        return ResponseEntity.ok(Map.of("message", "Employee deleted successfully"));
    }
}
