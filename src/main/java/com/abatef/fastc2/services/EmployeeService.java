package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.models.Employee;
import com.abatef.fastc2.models.Pharmacy;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final PharmacyService pharmacyService;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            UserService userService,
            PharmacyService pharmacyService) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
        this.pharmacyService = pharmacyService;
    }

    @Transactional
    public Employee createNewEmployee(EmployeeCreationRequest request, User principal) {
        User userInfo = userService.registerUser(request.getUser());
        Employee employee = new Employee();
        employee.setUser(userInfo);
        employee.setSalary(request.getSalary());
        employee.setAge(request.getAge());
        employee.setGender(request.getGender());
        Pharmacy pharmacy = pharmacyService.getPharmacyByIdOrThrow(request.getPharmacyId());
        employee.setPharmacy(pharmacy);
        employee = employeeRepository.save(employee);
        return employee;
    }
}
