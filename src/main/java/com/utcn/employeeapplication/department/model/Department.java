package com.utcn.employeeapplication.department.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.utcn.employeeapplication.employee.model.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer departmentId;
    private String description;
    private Integer parentId;
    private Integer managerId;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "department",
        cascade = CascadeType.ALL
    )
    @JsonManagedReference
    private List<Employee> employees ;

    public Department(String description, Integer parentId, Integer managerId) {
        this.description = description;
        this.parentId = parentId;
        this.managerId = managerId;
    }

    public Department() {

    }

    @Override
    public String toString() {
        return "Department{" +
                "departmentId=" + departmentId +
                ", description='" + description + '\'' +
                ", parentId=" + parentId +
                ", managerId=" + managerId +
                '}';
    }
}
