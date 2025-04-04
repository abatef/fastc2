package com.abatef.fastc2.exceptions;

import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.utils.Constants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DuplicateValueException extends RuntimeException {

    private ValueType valueType;
    private String value;

    public DuplicateValueException(ValueType valueType, String value) {
        super(String.format(Constants.DUPLICATE_VALUE, valueType, value));
        this.valueType = valueType;
        this.value = value;
    }
}
