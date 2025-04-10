package com.abatef.fastc2.exceptions;

import lombok.Getter;

public class DrugNotFoundException extends RuntimeException {
    @Getter private Integer drugId;

    public DrugNotFoundException(String message) {
        super(message);
    }

    public DrugNotFoundException(Integer id) {
        this.drugId = id;
    }
}
