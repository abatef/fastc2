package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    private @NotNull @NotEmpty String name;
    private @NotNull @NotEmpty String username;
    private @NotNull @NotEmpty @Email String email;
    private @NotNull @NotEmpty String phone;
    private @NotNull @NotEmpty String password;
    private @NotNull @NotEmpty UserRole role;
}
