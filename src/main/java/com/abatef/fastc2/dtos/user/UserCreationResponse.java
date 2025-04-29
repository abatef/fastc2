package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.dtos.auth.JwtAuthenticationResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationResponse {
    private UserDto user;
    private JwtAuthenticationResponse jwt;
}
