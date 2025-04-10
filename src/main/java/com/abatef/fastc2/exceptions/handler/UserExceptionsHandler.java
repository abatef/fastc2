package com.abatef.fastc2.exceptions.handler;

import com.abatef.fastc2.enums.ErrorType;
import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.exceptions.DuplicateValueException;
import com.abatef.fastc2.exceptions.ErrorResponse;
import com.abatef.fastc2.exceptions.NonExistingValueException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class UserExceptionsHandler {

    private ValueType getValueType(Exception e) {
        ValueType valueType = null;
        if (e.getMessage().startsWith("username")) {
            valueType = (ValueType.USERNAME);
        } else if (e.getMessage().startsWith("phone")) {
            valueType = (ValueType.PHONE);
        } else if (e.getMessage().startsWith("email")) {
            valueType = (ValueType.EMAIL);
        } else if (e.getMessage().startsWith("id")) {
            valueType = (ValueType.ID);
        }
        return valueType;
    }

    @ExceptionHandler(DuplicateValueException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateValueException(DuplicateValueException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setErrorType(ErrorType.DUPLICATE_KEY);
        errorResponse.setValueType(getValueType(e));
        errorResponse.setDetails(e.getValue());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NonExistingValueException.class)
    public ResponseEntity<ErrorResponse> handleNonExistingValueException(
            NonExistingValueException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setErrorType(ErrorType.NON_EXISTING_VALUE);
        errorResponse.setValueType(getValueType(e));
        errorResponse.setDetails(e.getValue());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
