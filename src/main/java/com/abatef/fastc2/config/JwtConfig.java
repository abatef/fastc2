package com.abatef.fastc2.config;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private final long accessTokenExpiration = 9 * 24 * 60 * 60 * 1000;
    private final long refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000;

    @Value("${jwt.secret}")
    private String secret;
}
