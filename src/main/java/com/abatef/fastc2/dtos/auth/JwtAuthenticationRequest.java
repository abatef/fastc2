package com.abatef.fastc2.dtos.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationRequest {
    @NotNull @NotEmpty private String username;
    @NotNull @NotEmpty private String password;
}
