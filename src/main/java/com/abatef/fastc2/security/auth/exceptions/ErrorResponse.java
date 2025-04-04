package com.abatef.fastc2.security.auth.exceptions;

import com.abatef.fastc2.enums.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private ErrorType errorType;
}
