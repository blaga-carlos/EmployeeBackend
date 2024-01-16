package com.utcn.employeeapplication.department.controller;

import com.utcn.employeeapplication.department.model.Department;
import com.utcn.employeeapplication.department.repository.DepartmentRepository;
import com.utcn.employeeapplication.employee.model.Employee;
import com.utcn.employeeapplication.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class DepartmentController {
    @Autowired
    private EmployeeRepository employeeService;

    @Autowired
    private DepartmentRepository departmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/saveDepartment")
    public String saveDepartment(@RequestBody Department department) {
        System.out.println("Department save called...");

        Department departmentIn = new Department(department.getDescription(), department.getParentId(), department.getManagerId());

        List<Employee> employees = new ArrayList<>();
        for (Employee employeeIn : department.getEmployees()) {
            Employee employee = new Employee(
                    employeeIn.getName(),
                    employeeIn.getManagerId(),
                    employeeIn.getEmail(),
                    this.passwordEncoder.encode(employeeIn.getPassword()));
            employee.setDepartment(departmentIn);
            employees.add(employee);
        }

        departmentIn.setEmployees(employees);

        Department departmentOut = departmentService.save(departmentIn);
        System.out.println("Department out :: " + departmentOut);

        System.out.println("Saved!!!");
        return "Department saved!!!";
    }

    @GetMapping("/getAllDepartments")
    public Iterable<Department> getAllDepartments(){
        return departmentService.findAll();
    }


    @GetMapping("/getDepartment/{id}")
    public ResponseEntity<Department> getDepartment(@PathVariable Integer id) {
        try {
            Department departmentOut = departmentService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Department not found"));

            System.out.println("\nDepartment details :: \n" + departmentOut);
            System.out.println("\nList of Employees :: \n" + departmentOut.getEmployees());
            System.out.println("\nDone!!!");

            return new ResponseEntity<>(departmentOut, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteDepartment/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Integer id) {
        try {
            Department departmentToDelete = departmentService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Department not found"));

            List<Employee> employees = departmentToDelete.getEmployees();
            if (employees != null) {
                for (Employee employee : employees) {
                    employee.setDepartment(null);
                    employeeService.save(employee);
                }
            }

            departmentService.deleteById(id);

            return new ResponseEntity<>("Department deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Department not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateDepartment/{id}")
    public ResponseEntity<String> updateDepartment(@PathVariable Integer id, @RequestBody Department updatedDepartment) {
        try {
            Department existingDepartment = departmentService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Department not found"));

            existingDepartment.setDescription(updatedDepartment.getDescription());
            existingDepartment.setManagerId(updatedDepartment.getManagerId());
            existingDepartment.setParentId(updatedDepartment.getParentId());

            departmentService.save(existingDepartment);

            return new ResponseEntity<>("Department updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Department not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getEmployeesByDepartment/{departmentId}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable Integer departmentId) {
        try {
            Department department = departmentService.findById(departmentId)
                    .orElseThrow(() -> new NoSuchElementException("Department not found"));

            List<Employee> employees = department.getEmployees();

            if (employees != null && !employees.isEmpty()) {
                System.out.println("\nList of Employees in Department :: \n" + employees);
                return new ResponseEntity<>(employees, HttpStatus.OK);
            } else {
                System.out.println("\nNo Employees found in Department.");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

