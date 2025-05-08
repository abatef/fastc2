package com.abatef.fastc2.exceptions.handler;

import com.abatef.fastc2.enums.ErrorType;
import com.abatef.fastc2.exceptions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class UserExceptionsHandler {

    @ExceptionHandler(DuplicateValueException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateValueException(DuplicateValueException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setErrorType(ErrorType.DUPLICATE_KEY);
        errorResponse.setValueType(ErrorResponse.getValueType(e));
        errorResponse.setDetails(e.getValue());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NonExistingValueException.class)
    public ResponseEntity<ErrorResponse> handleNonExistingValueException(
            NonExistingValueException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setErrorType(ErrorType.NON_EXISTING_VALUE);
        errorResponse.setValueType(ErrorResponse.getValueType(e));
        errorResponse.setDetails(e.getValue());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(exception = {NotOwnerException.class, NotEmployeeException.class})
    public ResponseEntity<ErrorResponse> handleNotOwnerException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        if (e instanceof NotOwnerException) {
            errorResponse.setErrorType(ErrorType.NOT_OWNER_OF_PHARMACY);
        } else if (e instanceof NotEmployeeException) {
            errorResponse.setErrorType(ErrorType.NOT_EMPLOYEE_OR_MANAGER);
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
