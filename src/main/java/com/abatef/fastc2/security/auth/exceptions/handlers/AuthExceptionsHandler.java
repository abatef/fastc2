package com.abatef.fastc2.security.auth.exceptions.handlers;

import com.abatef.fastc2.enums.ErrorType;
import com.abatef.fastc2.exceptions.ErrorResponse;
import com.abatef.fastc2.security.auth.exceptions.ExpiredJwtException;
import com.abatef.fastc2.security.auth.exceptions.UsedRefreshTokenException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class AuthExceptionsHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setMessage("The Refresh Token has expired");
        errorResponse.setErrorType(ErrorType.EXPIRED_JWT);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(exception = {UsedRefreshTokenException.class, MalformedJwtException.class})
    public ResponseEntity<ErrorResponse> handleUsedRefreshTokenException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setMessage("The Refresh Token is used before");
        errorResponse.setErrorType(ErrorType.USED_REFRESH_TOKEN);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(
            io.jsonwebtoken.ExpiredJwtException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setMessage("The Access Token has expired, try using the refresh token");
        errorResponse.setErrorType(ErrorType.EXPIRED_JWT);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
