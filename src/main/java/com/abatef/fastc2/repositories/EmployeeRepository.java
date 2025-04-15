package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee getEmployeeById(Integer id);
}
