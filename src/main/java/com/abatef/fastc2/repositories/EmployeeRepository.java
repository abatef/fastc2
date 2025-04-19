package com.abatef.fastc2.repositories;

import com.abatef.fastc2.models.Employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee getEmployeeById(Integer id);

    Optional<Employee> getEmployeeByIdAndPharmacy_Id(Integer id, Integer pharmacyId);

    Page<Employee> findAllByPharmacy_Id(Integer pharmacyId, Pageable pageable);

    Page<Employee> getEmployeesByPharmacy_Id(Integer pharmacyId, Pageable pageable);
}
