package com.abatef.fastc2.models.pharmacy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "sales_receipt")
public class ReceiptItem {
    @EmbeddedId private ReceiptItemId id;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pharmacy_drug_id", nullable = false)
    private PharmacyDrug pharmacyDrug;

    @NotNull
    @ColumnDefault("0.0")
    @Column(name = "amount_due", nullable = false)
    private Float amountDue;

    @Column(name = "discount")
    private Float discount;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "units", nullable = false)
    private Short units;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "pack", nullable = false)
    private Short pack;
}
