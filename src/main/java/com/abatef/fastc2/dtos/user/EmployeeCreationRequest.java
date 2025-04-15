package com.abatef.fastc2.dtos.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreationRequest {
    @NotNull private UserCreationRequest user;

    @NotNull
    @Min(18)
    private Short age;

    private String gender;
    private Integer roleId;
    private Float salary;
    private Integer pharmacyId;
    private Integer shiftId;
}
