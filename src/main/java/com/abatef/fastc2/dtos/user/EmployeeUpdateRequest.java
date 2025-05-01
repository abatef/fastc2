package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.models.shift.Shift;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateRequest {
    @NotNull private Integer id;

    @Min(18)
    private Short age;

    private String gender;
    private Float salary;
    private Shift shift;
    private UserRole role;
}
