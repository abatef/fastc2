package com.abatef.fastc2.models.pharmacy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItemId implements Serializable {
    private static final long serialVersionUID = -4513323556034627185L;

    @Column(name = "receipt_id")
    private Integer receiptId;

    @Column(name = "pharmacy_drug_id")
    private Integer pharmacyDrugId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceiptItemId that = (ReceiptItemId) o;
        return Objects.equals(receiptId, that.receiptId) &&
                Objects.equals(pharmacyDrugId, that.pharmacyDrugId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiptId, pharmacyDrugId);
    }
}