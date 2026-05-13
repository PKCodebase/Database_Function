package com.database.service.impl;

import com.database.dto.EmployeeDTO;
import com.database.exception.NotFoundException;
import com.database.repo.EmployeeRepository;
import com.database.service.EmployeeService;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;

    }


    @Override
    @Cacheable(value = "employees")
    public List<EmployeeDTO> getAllEmployees() {
        log.debug("Calling DB function → get_employee()");

        List<Object[]> rows = employeeRepository.getAllEmployees();

        if (rows == null || rows.isEmpty()) {
            log.warn("No employees found in database");
            throw new NotFoundException("No employees found");
        }

        log.debug("Rows fetched from DB → {}", rows.size());

        List<EmployeeDTO> employees = rows.stream()
                .map(employee -> new EmployeeDTO(
                        (String) employee[0],
                        (String) employee[1],
                        (String) employee[2],
                        (String) employee[3]
                ))
                .collect(Collectors.toList());

        log.info("Employees mapped successfully → total: {}", employees.size());
        return employees;
    }

    @Override
    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public String addEmployee(EmployeeDTO employeeDTO) {
        log.info("Calling DB function -> Save Employee");

        if (employeeDTO == null) {
            log.warn("EmployeeDTO is null");
            throw new NotFoundException("Employee data cannot be null");
        }

        String employees = employeeRepository.addEmployee(
                employeeDTO.getEmpName(),
                employeeDTO.getDesignation(),
                employeeDTO.getDepartment(),
                employeeDTO.getMobile()
        );
        log.info("Employee saved successfully -> {}", employeeDTO.getEmpName());
        return employees;
    }

    @Override
    @Cacheable(value = "employee", key = "#id")
    public EmployeeDTO getEmployeeById(Long id) {
        log.info("Fetching employee by id → {}", id);
        return employeeRepository.findById(id)
                .map(employee -> {
                    log.info("Employee fetched successfully → id: {}", id);
                    return new EmployeeDTO(
                            employee.getEmpName(),
                            employee.getDesignation(),
                            employee.getDepartment(),
                            employee.getMobile()
                    );
                })
                .orElseThrow(() -> {
                    log.warn("Employee not found → id: {}", id);
                    return new NotFoundException("Employee not found with id: " + id);
                });
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "employee", key = "#id"),       // ✅ Clear single cache
            @CacheEvict(value = "employees", allEntries = true)  // ✅ Clear list cache
    })
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        log.info("Updating employee → id: {}", id);
        employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found for update → id: {}", id);
                    return new NotFoundException("Employee not found with id: " + id);
                });

        employeeRepository.updateEmployee(
                id,
                employeeDTO.getEmpName(),
                employeeDTO.getDesignation(),
                employeeDTO.getDepartment(),
                employeeDTO.getMobile()
        );
        log.info("Employee updated successfully → id: {}", id);
        return employeeDTO;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "employee", key = "#id"),        // ✅ Clear single cache
            @CacheEvict(value = "employees", allEntries = true)   // ✅ Clear list cache
    })
    public void deleteEmployeeById(Long id) {
        log.info("Deleting employee → id: {}", id);
        employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found for deletion → id: {}", id);
                    return new NotFoundException("Employee not found with id: " + id);
                });
        employeeRepository.deleteById(id);
        log.info("Employee deleted successfully → id: {}", id);
    }


}