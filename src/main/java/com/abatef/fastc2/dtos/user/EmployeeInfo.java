package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.models.shift.Shift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfo {
    private UserInfo user;
    private Short age;
    private String gender;
    private Float salary;
    private Shift shift;
    private PharmacyInfo pharmacy;
    private Instant createdAt;
    private Instant updatedAt;
}
