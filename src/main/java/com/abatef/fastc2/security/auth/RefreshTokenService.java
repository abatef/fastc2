package com.abatef.fastc2.security.auth;

import com.abatef.fastc2.security.auth.exceptions.ExpiredJwtException;
import com.abatef.fastc2.security.auth.exceptions.UsedRefreshTokenException;
import com.abatef.fastc2.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;

@Service
public class RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken generateRefreshToken(UserDetails userDetails) {
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        token.setUsername(userDetails.getUsername());
        token.setExpiryDate(jwtUtil.getExpirationDateFromToken(refreshToken).toInstant());
        token.setUsed(false);
        return refreshTokenRepository.save(token);
    }

    public RefreshToken validateRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken);
        if (token == null) {
            throw new InvalidOneTimeTokenException("Invalid refresh token");
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new ExpiredJwtException("Expired Refresh Token");
        }

        if (token.getUsed()) {
            throw new UsedRefreshTokenException("Used Refresh Token");
        }

        token.setUsed(true);
        refreshTokenRepository.save(token);
        return token;
    }
}
