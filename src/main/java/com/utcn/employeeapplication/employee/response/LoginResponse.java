package com.utcn.employeeapplication.employee.response;

import com.utcn.employeeapplication.employee.model.Employee;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String message;
    private Employee user;

    public LoginResponse(String message, Employee user) {
        this.message = message;
        this.user = user;
    }
}
