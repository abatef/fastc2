package com.abatef.fastc2.exceptions;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException {
    private final Integer employeeId;
    public EmployeeNotFoundException(Integer employeeId) {
        this.employeeId = employeeId;
    }
}
