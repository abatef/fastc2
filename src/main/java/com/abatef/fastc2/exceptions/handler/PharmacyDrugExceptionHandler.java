package com.abatef.fastc2.exceptions.handler;

import com.abatef.fastc2.enums.ErrorType;
import com.abatef.fastc2.exceptions.DrugNotFoundException;
import com.abatef.fastc2.exceptions.ErrorResponse;
import com.abatef.fastc2.exceptions.InsufficientStockException;
import com.abatef.fastc2.exceptions.PharmacyDrugNotFoundException;

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
    @ExceptionHandler(exception = PharmacyDrugNotFoundException.class)
    public ResponseEntity<PharmacyDrugError> handle(PharmacyDrugNotFoundException e) {
        PharmacyDrugError error = new PharmacyDrugError();
        error.setId(e.getId());
        error.setPharmacyId(e.getPharmacyId());
        error.setDrugId(e.getDrugId());
        error.setMessage(e.getMessage());
        error.setWhy(PharmacyDrugNotFoundException.Why.NONEXISTENT_DRUG_PHARMACY);
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

    @ExceptionHandler(exception = InsufficientStockException.class)
    public ResponseEntity<PharmacyDrugError> insufficientStock(InsufficientStockException e) {
        PharmacyDrugError error = new PharmacyDrugError();
        error.setPharmacyId(e.getPharmacyId());
        error.setDrugId(e.getDrugId());
        error.setMessage(e.getMessage());
        error.setWhy(PharmacyDrugNotFoundException.Why.INSUFFICIENT_STOCK);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PharmacyDrugError {
        Integer id;
        Integer drugId;
        Integer pharmacyId;
        String message;
        PharmacyDrugNotFoundException.Why why;
    }
}
