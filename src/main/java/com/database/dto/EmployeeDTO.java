package com.database.dto;

import java.io.Serializable;

public class EmployeeDTO implements Serializable {

    private String empName;
    private String designation;
    private String department;
    private String mobile;

    public EmployeeDTO() {

    }

    // Constructor
    public EmployeeDTO(String empName, String designation,
                       String department, String mobile) {
        this.empName = empName;
        this.designation = designation;
        this.department = department;
        this.mobile = mobile;
    }

    // Getters & Setters
    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "empName='" + empName + '\'' +
                ", designation='" + designation + '\'' +
                ", department='" + department + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
