package com.abatef.fastc2.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PharmacyDrugNotFoundException extends RuntimeException {
    private Integer id;
    private Why why;

    public PharmacyDrugNotFoundException(String message) {
        super(message);
    }

    public PharmacyDrugNotFoundException(Integer id, Why why) {
        this.id = id;
        this.why = why;
    }

    public PharmacyDrugNotFoundException(Integer id) {
        this.id = id;
    }

    public enum Why {
        NONEXISTENT_DRUG,
        NONEXISTENT_PHARMACY,
        NONEXISTENT_DRUG_PHARMACY,
        NONEXISTENT_WITH_EXPIRY_DATE
    }
}
