package com.abatef.fastc2.security.auth;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    RefreshToken findRefreshTokenByToken(@NotNull String token);

    RefreshToken findByToken(@NotNull String token);
}
