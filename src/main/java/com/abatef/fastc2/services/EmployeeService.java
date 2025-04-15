package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.dtos.user.EmployeeInfo;
import com.abatef.fastc2.exceptions.EmployeeNotFoundException;
import com.abatef.fastc2.models.Employee;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.repositories.EmployeeRepository;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserService userService;
    private final PharmacyService pharmacyService;
    private final ShiftService shiftService;
    private final ModelMapper modelMapper;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            UserService userService,
            PharmacyService pharmacyService, ShiftService shiftService, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
        this.pharmacyService = pharmacyService;
        this.shiftService = shiftService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public EmployeeInfo createNewEmployee(EmployeeCreationRequest request, User principal) {
        User userInfo = userService.registerUser(request.getUser());
        Employee employee = new Employee();
        employee.setUser(userInfo);
        employee.setSalary(request.getSalary());
        employee.setAge(request.getAge());
        employee.setGender(request.getGender());
        Pharmacy pharmacy = pharmacyService.getPharmacyByIdOrThrow(request.getPharmacyId());
        employee.setPharmacy(pharmacy);
        employee = employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeInfo.class);
    }

    public Employee getEmployeeByIdOrThrow(Integer employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            return employee.get();
        }
        throw new EmployeeNotFoundException(employeeId);
    }

    public EmployeeInfo getEmployeeInfoById(Integer employeeId) {
        return modelMapper.map(getEmployeeByIdOrThrow(employeeId), EmployeeInfo.class);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public EmployeeInfo updateEmployee(EmployeeInfo employeeInfo, User principal) {
        Employee employee = getEmployeeByIdOrThrow(employeeInfo.getUser().getId());
        if (employeeInfo.getSalary() != null) {
            employee.setSalary(employeeInfo.getSalary());
        }

        if (employeeInfo.getAge() != null) {
            employee.setAge(employeeInfo.getAge());
        }

        if (employeeInfo.getGender() != null) {
            employee.setGender(employeeInfo.getGender());
        }

        if (employeeInfo.getShift() != null) {
            Shift shift = shiftService.getByIdOrThrow(employeeInfo.getShift().getId());
            employee.setShift(shift);
        }

        if (employeeInfo.getUser().getRole() != null) {
            employee.getUser().setRole(employeeInfo.getUser().getRole());
        }
        employee = employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeInfo.class);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public void deleteEmployee(Integer employeeId, User principal) {
        employeeRepository.deleteById(employeeId);
    }
}
