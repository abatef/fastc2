package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.dtos.user.EmployeeInfo;
import com.abatef.fastc2.dtos.user.EmployeeUpdateRequest;
import com.abatef.fastc2.models.Employee;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.EmployeeService;

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
    public ResponseEntity<EmployeeInfo> createEmployee(
            @RequestBody EmployeeCreationRequest employee, @AuthenticationPrincipal User user) {
        EmployeeInfo info = employeeService.createNewEmployee(employee, user);
        return ResponseEntity.ok(info);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeInfo> getEmployee(@PathVariable Integer id) {
        EmployeeInfo info = employeeService.getEmployeeInfoById(id);
        return ResponseEntity.ok(info);
    }

    @PatchMapping
    public ResponseEntity<EmployeeInfo> updateEmployee(
            @RequestBody EmployeeUpdateRequest employee, @AuthenticationPrincipal User user) {
        EmployeeInfo info = employeeService.updateEmployee(employee, user);
        return ResponseEntity.ok(info);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable("id") Integer id, @AuthenticationPrincipal User user) {
        employeeService.deleteEmployee(id, user);
        return ResponseEntity.noContent().build();
    }
}
