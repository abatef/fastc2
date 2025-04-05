package com.abatef.fastc2.exceptions;


import com.abatef.fastc2.models.PharmacyDrugId;
import lombok.Getter;

@Getter
public class PharmacyDrugNotFoundException extends RuntimeException {
    private PharmacyDrugId id;
    public PharmacyDrugNotFoundException(String message) {
        super(message);
    }

    public PharmacyDrugNotFoundException(PharmacyDrugId id) {
        this.id = id;
    }
}
