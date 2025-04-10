package com.abatef.fastc2.exceptions;

import com.abatef.fastc2.enums.ErrorType;
import com.abatef.fastc2.enums.ValueType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private ErrorType errorType;
    private String errorMessage;
    private ValueType valueType;
    private String details;
    private String message;
}
