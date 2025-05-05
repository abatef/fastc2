package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.dtos.user.EmployeeDto;
import com.abatef.fastc2.dtos.user.EmployeeUpdateRequest;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Register a new Employee in Pharmacy")
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(
            @Valid @RequestBody EmployeeCreationRequest employee,
            @AuthenticationPrincipal User user) {
        EmployeeDto info = employeeService.createNewEmployee(employee, user);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Get Employee Info by his Id")
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Integer id) {
        EmployeeDto info = employeeService.getEmployeeInfoById(id);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Update Employee Info By his Id")
    @PatchMapping
    public ResponseEntity<EmployeeDto> updateEmployee(
            @Valid @RequestBody EmployeeUpdateRequest employee,
            @AuthenticationPrincipal User user) {
        EmployeeDto info = employeeService.updateEmployee(employee, user);
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Delete Employee By his Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        employeeService.deleteEmployee(id, user);
        return ResponseEntity.noContent().build();
    }
}
