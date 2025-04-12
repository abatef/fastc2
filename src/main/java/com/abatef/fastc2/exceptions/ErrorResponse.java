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

    public static ValueType getValueType(Exception e) {
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
}
