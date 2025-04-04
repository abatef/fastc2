package com.abatef.fastc2.security.auth.exceptions;

public class UsedRefreshTokenException extends RuntimeException {
    public UsedRefreshTokenException(String message) {
        super(message);
    }
}
