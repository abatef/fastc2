package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.dtos.user.EmployeeDto;
import com.abatef.fastc2.dtos.user.EmployeeUpdateRequest;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.EmployeeService;

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

    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(
            @Valid @RequestBody EmployeeCreationRequest employee,
            @AuthenticationPrincipal User user) {
        EmployeeDto info = employeeService.createNewEmployee(employee, user);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Integer id) {
        EmployeeDto info = employeeService.getEmployeeInfoById(id);
        return ResponseEntity.ok(info);
    }

    @PatchMapping
    public ResponseEntity<EmployeeDto> updateEmployee(
            @Valid @RequestBody EmployeeUpdateRequest employee,
            @AuthenticationPrincipal User user) {
        EmployeeDto info = employeeService.updateEmployee(employee, user);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        employeeService.deleteEmployee(id, user);
        return ResponseEntity.noContent().build();
    }
}
