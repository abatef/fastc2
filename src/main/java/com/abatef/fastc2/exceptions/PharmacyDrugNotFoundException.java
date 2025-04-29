package com.abatef.fastc2.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PharmacyDrugNotFoundException extends RuntimeException {
    private Integer id;
    private Integer pharmacyId;
    private Integer drugId;
    private Why why;

    public PharmacyDrugNotFoundException(String message) {
        super(message);
    }

    public PharmacyDrugNotFoundException(Integer id, Why why) {
        this.id = id;
        this.why = why;
    }

    public PharmacyDrugNotFoundException(Integer pharmacyId, Integer drugId, Why why) {
        super(String.format("Pharmacy with id: %d doesn't have drug with id: %d.", pharmacyId, drugId));
        this.pharmacyId = pharmacyId;
        this.drugId = drugId;
        this.why = why;
    }

    public PharmacyDrugNotFoundException(Integer id) {
        super(String.format("Pharmacy Drug with id %s not found", id));
        this.id = id;
    }

    public enum Why {
        NONEXISTENT_DRUG,
        NONEXISTENT_PHARMACY,
        NONEXISTENT_DRUG_PHARMACY,
        NONEXISTENT_WITH_EXPIRY_DATE,
        INSUFFICIENT_STOCK
    }
}
