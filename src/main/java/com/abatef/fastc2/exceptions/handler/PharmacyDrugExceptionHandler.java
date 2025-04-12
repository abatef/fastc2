package com.abatef.fastc2.exceptions.handler;

import com.abatef.fastc2.enums.ErrorType;
import com.abatef.fastc2.exceptions.DrugNotFoundException;
import com.abatef.fastc2.exceptions.ErrorResponse;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;
import com.abatef.fastc2.models.pharmacy.PharmacyDrugId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class PharmacyDrugExceptionHandler {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class PharmacyDrugError {
        PharmacyDrugId id;
        String message;
        PharmacyDrugNotFoundException.Why why;
    }

    @ExceptionHandler(exception = PharmacyDrugNotFoundException.class)
    public ResponseEntity<PharmacyDrugError> handle(PharmacyDrugNotFoundException e) {
        PharmacyDrugError error = new PharmacyDrugError();
        error.setId(e.getId());
        error.setMessage(e.getMessage());
        error.setWhy(e.getWhy());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(exception = DrugNotFoundException.class)
    public ResponseEntity<ErrorResponse> drugNotFound(DrugNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(e.getMessage());
        errorResponse.setErrorType(ErrorType.NON_EXISTING_VALUE);
        errorResponse.setValueType(ErrorResponse.getValueType(e));
        errorResponse.setDetails(e.getDrugId().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
