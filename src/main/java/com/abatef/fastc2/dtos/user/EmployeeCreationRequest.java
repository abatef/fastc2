package com.abatef.fastc2.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreationRequest {
    private UserCreationRequest user;
    private Short age;
    private String gender;
    private Integer roleId;
    private Float salary;
    private Integer pharmacyId;
}
