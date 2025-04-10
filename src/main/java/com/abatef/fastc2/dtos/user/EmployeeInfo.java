package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfo {
    private UserInfo user;
    private Short age;
    private String gender;
    private Float salary;
    private PharmacyInfo pharmacy;
    private Instant createdAt;
    private Instant updatedAt;
}
