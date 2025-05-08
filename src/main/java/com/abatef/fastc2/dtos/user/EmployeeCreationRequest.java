package com.abatef.fastc2.dtos.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreationRequest {
    @NotNull @Valid private UserCreationRequest user;

    @NotNull
    @Min(18)
    private Short age;

    private String gender;
    private Float salary;
    @NotNull private Integer pharmacyId;
    @NotNull private Integer shiftId;
}
