package com.abatef.fastc2.exceptions;

import lombok.Getter;

@Getter
public class InsufficientStockException extends RuntimeException {
    private int drugId;
    private int pharmacyId;
    private int quantity;

    public InsufficientStockException(int drugId, int pharmacyId, int quantity) {
        super(
                String.format(
                        "Pharmacy: %d doesn't have the required quantity %d of the the drug %d.",
                        pharmacyId, quantity, drugId));
        this.drugId = drugId;
        this.pharmacyId = pharmacyId;
        this.quantity = quantity;
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
