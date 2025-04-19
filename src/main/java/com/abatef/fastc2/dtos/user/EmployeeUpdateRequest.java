package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.models.shift.Shift;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateRequest {
    private Integer id;
    private Short age;
    private String gender;
    private Float salary;
    private Shift shift;
    private UserRole role;
}
