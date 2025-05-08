package com.abatef.fastc2.dtos.user;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username can only contain letters, numbers, and underscores")
    private @NotNull @NotEmpty String username;

    private @NotNull @NotEmpty @Email String email;
    private @NotNull @NotEmpty String password;
    private Boolean managedUser = false;
}
