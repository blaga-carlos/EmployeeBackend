package com.utcn.employeeapplication;

import com.utcn.employeeapplication.department.model.Department;
import com.utcn.employeeapplication.department.repository.DepartmentRepository;
import com.utcn.employeeapplication.employee.model.Employee;
import org.hibernate.id.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class EmployeeapplicationApplication {

	public static void main(String[] args) {
				SpringApplication.run(EmployeeapplicationApplication.class, args);
	}

}
