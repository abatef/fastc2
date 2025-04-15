package com.abatef.fastc2.exceptions;

import com.abatef.fastc2.enums.ValueType;
import com.abatef.fastc2.utils.Values;

import lombok.Getter;

@Getter
public class DrugNotFoundException extends RuntimeException {
    private final Integer drugId;

    public DrugNotFoundException(Integer id) {
        super(String.format(Values.NONEXISTENT_VALUE, ValueType.ID.name(), id));
        this.drugId = id;
    }
}
