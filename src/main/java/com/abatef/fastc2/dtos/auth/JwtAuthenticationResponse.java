package com.abatef.fastc2.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private final long accessTokenExpiration = 15 * 60 * 1000;
    private String refreshToken;
    private final long refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000;
}
