package com.abatef.fastc2.exceptions;

import lombok.Getter;

@Getter
public class PharmacyNotFoundException extends RuntimeException {
    private final Integer pharmacyId;

    public PharmacyNotFoundException(Integer pharmacyId) {
        this.pharmacyId = pharmacyId;
    }
}
