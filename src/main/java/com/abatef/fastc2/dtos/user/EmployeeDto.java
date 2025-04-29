package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.models.shift.Shift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private UserDto user;
    private Short age;
    private String gender;
    private Float salary;
    private Shift shift;
    private PharmacyDto pharmacy;
    private Instant createdAt;
    private Instant updatedAt;
}
