package com.database.service;

import com.database.dto.EmployeeDTO;
import com.database.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDTO> getAllEmployees();
    String addEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO getEmployeeById(Long id);
    EmployeeDTO updateEmployee(Long id,EmployeeDTO employeeDTO);
    void deleteEmployeeById(Long id);

}
