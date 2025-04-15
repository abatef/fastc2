package com.abatef.fastc2.exceptions;

import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.utils.Values;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NonExistingValueException extends RuntimeException {

    private ValueType valueType;
    private String value;

    public NonExistingValueException(ValueType valueType, String value) {
        super(String.format(Values.NONEXISTENT_VALUE, valueType.name().toLowerCase(), value));
        this.valueType = valueType;
        this.value = value;
    }
}
