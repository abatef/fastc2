package com.abatef.fastc2.services;

import com.abatef.fastc2.dtos.user.EmployeeCreationRequest;
import com.abatef.fastc2.dtos.user.EmployeeDto;
import com.abatef.fastc2.dtos.user.EmployeeUpdateRequest;
import com.abatef.fastc2.exceptions.EmployeeNotFoundException;
import com.abatef.fastc2.exceptions.PharmacyNotFoundException;
import com.abatef.fastc2.models.Employee;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.repositories.EmployeeRepository;
import com.abatef.fastc2.repositories.PharmacyRepository;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PharmacyRepository pharmacyRepository;
    private final UserService userService;
    private final ShiftService shiftService;
    private final ModelMapper modelMapper;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            UserService userService,
            PharmacyRepository pharmacyRepository,
            ShiftService shiftService,
            ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.userService = userService;
        this.pharmacyRepository = pharmacyRepository;
        this.shiftService = shiftService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public EmployeeDto createNewEmployee(EmployeeCreationRequest request, User principal) {
        request.getUser().setManagedUser(true);
        User userInfo = userService.registerUser(request.getUser());
        Employee employee = new Employee();
        employee.setUser(userInfo);
        employee.setSalary(request.getSalary());
        employee.setAge(request.getAge());
        employee.setGender(request.getGender());
        Shift shift = shiftService.getByIdOrThrow(request.getShiftId());
        employee.setShift(shift);
        Pharmacy pharmacy =
                pharmacyRepository
                        .getPharmacyById(request.getPharmacyId())
                        .orElseThrow(() -> new PharmacyNotFoundException(request.getPharmacyId()));
        employee.setPharmacy(pharmacy);
        employee = employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeDto.class);
    }

    public Employee getEmployeeByIdOrThrow(Integer employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            return employee.get();
        }
        throw new EmployeeNotFoundException(employeeId);
    }

    public EmployeeDto getEmployeeInfoById(Integer employeeId) {
        return modelMapper.map(getEmployeeByIdOrThrow(employeeId), EmployeeDto.class);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public EmployeeDto updateEmployee(EmployeeUpdateRequest employeeInfo, User principal) {
        Employee employee = getEmployeeByIdOrThrow(employeeInfo.getId());
        if (employeeInfo.getSalary() != null) {
            employee.setSalary(employeeInfo.getSalary());
        }

        if (employeeInfo.getAge() != null) {
            employee.setAge(employeeInfo.getAge());
        }

        if (employeeInfo.getGender() != null) {
            employee.setGender(employeeInfo.getGender());
        }

        if (employeeInfo.getShiftId() != null) {
            Shift shift = shiftService.getByIdOrThrow(employeeInfo.getShiftId());
            employee.setShift(shift);
        }

        if (employeeInfo.getRole() != null) {
            employee.getUser().setRole(employeeInfo.getRole());
        }
        employee = employeeRepository.save(employee);
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public void deleteEmployee(Integer employeeId, User principal) {
        employeeRepository.deleteById(employeeId);
    }
}
