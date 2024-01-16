package com.utcn.employeeapplication.employee.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.utcn.employeeapplication.department.model.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    private String name;
    private Integer managerId;
    private String email;
    private String password;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "departmentId")
    @JsonBackReference
    private Department department;

    @JsonProperty("departmentId")
    public Integer getDepartmentId() {
        return (department != null) ? department.getDepartmentId() : null;
    }
    public Employee(String name, Integer managerId, String email, String password) {
        this.name = name;
        this.managerId = managerId;
        this.email = email;
        this.password = password;
    }

    public Employee() {

    }

    @Override
    public String toString() {
        return "Employee{" +
                "ID=" + Id +
                ", name='" + name + '\'' +
                ", managerId=" + managerId +
                ", email='" + email + '\'' +
                '}';
    }
}
