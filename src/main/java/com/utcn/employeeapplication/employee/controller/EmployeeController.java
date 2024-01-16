package com.utcn.employeeapplication.employee.controller;

import com.utcn.employeeapplication.department.model.Department;
import com.utcn.employeeapplication.department.repository.DepartmentRepository;
import com.utcn.employeeapplication.employee.DTO.LoginDTO;
import com.utcn.employeeapplication.employee.model.Employee;
import com.utcn.employeeapplication.employee.repository.EmployeeRepository;
import com.utcn.employeeapplication.employee.response.LoginResponse;
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
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeService;

    @Autowired
    private DepartmentRepository departmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/saveEmployee")
    public String saveEmployee(@RequestBody Employee employee) {
        System.out.println("Employee save called...");

        Department departmentTemp = departmentService.getById(Integer.valueOf(employee.getId()));

        List<Employee> employees = new ArrayList<>();

        Employee employeeIn = new Employee(
                employee.getName(),
                employee.getManagerId(),
                employee.getEmail(),
                this.passwordEncoder.encode(employee.getPassword())
        );
        employeeIn.setDepartment(departmentTemp);
        employees.add(employeeIn);

        departmentTemp.setEmployees(employees);

        departmentService.save(departmentTemp);

        System.out.println("Saved!!!");
        return "Employee saved!!!";
    }

    @GetMapping("/getAllEmployees")
    public Iterable<Employee> getAllEmployees(){
        return employeeService.findAll();
    }

    @GetMapping("/getEmployee/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Integer id) {
        try {
            Employee employeeOut = employeeService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Employee not found"));

            System.out.println("\nEmployee details :: \n" + employeeOut);
            System.out.println("\nDepartment details :: \n" + employeeOut.getDepartment());

            return new ResponseEntity<>(employeeOut, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteEmployee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Integer id) {
        try {
            Employee employeeToDelete = employeeService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Employee not found"));

            Department department = employeeToDelete.getDepartment();

            if (department != null) {
                department.getEmployees().remove(employeeToDelete);
                departmentService.save(department);
            }

            employeeToDelete.setDepartment(null);
            employeeService.save(employeeToDelete);

            employeeService.deleteById(id);

            return new ResponseEntity<>("Employee deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateEmployee/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable Integer id, @RequestBody Employee updatedEmployee) {
        try {
            Employee existingEmployee = employeeService.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Employee not found"));

            existingEmployee.setName(updatedEmployee.getName());
            existingEmployee.setManagerId(updatedEmployee.getManagerId());
            existingEmployee.setEmail(updatedEmployee.getEmail());

            employeeService.save(existingEmployee);

            return new ResponseEntity<>("Employee updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/loginEmployee")
    public ResponseEntity<?> loginEmployee(@RequestBody LoginDTO loginEmployee) {
        try {
            Employee existingEmployee = employeeService.findByEmail(loginEmployee.getEmail());

            if (existingEmployee != null) {
                if (passwordEncoder.matches(loginEmployee.getPassword(), existingEmployee.getPassword())) {
                    return new ResponseEntity<>(new LoginResponse("Login successful", existingEmployee), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Incorrect password", HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>("Employee not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/registerEmployee")
    public ResponseEntity<String> registerEmployee(@RequestBody Employee employee) {
        try {
            Employee existingEmployee = employeeService.findByEmail(employee.getEmail());

            if (existingEmployee == null) {
                employee.setManagerId(0);
                employee.setPassword(passwordEncoder.encode(employee.getPassword()));

                employeeService.save(employee);

                return new ResponseEntity<>("Registration successful", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Email is already registered", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/assignEmployeeToDepartment/{employeeId}/{departmentId}")
    public ResponseEntity<String> assignEmployeeToDepartment(
            @PathVariable Integer employeeId,
            @PathVariable Integer departmentId) {
        try {
            Employee employee = employeeService.findById(employeeId)
                    .orElseThrow(() -> new NoSuchElementException("Employee not found"));

            Department department = departmentService.findById(departmentId)
                    .orElseThrow(() -> new NoSuchElementException("Department not found"));

            Department existingDepartment = employee.getDepartment();
            if (existingDepartment != null) {
                existingDepartment.getEmployees().remove(employee);
                departmentService.save(existingDepartment);
            }

            employee.setDepartment(department);
            department.getEmployees().add(employee);

            employeeService.save(employee);
            departmentService.save(department);

            return new ResponseEntity<>("Employee assigned to department successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Employee or Department not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
