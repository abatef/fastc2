package com.abatef.fastc2.models.pharmacy;

import com.abatef.fastc2.enums.ItemStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "sales_receipt")
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItem {
    @EmbeddedId private ReceiptItemId id;

    @MapsId("receiptId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "receipt_id", nullable = false)
    private Receipt receipt;

    @MapsId("pharmacyDrugId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ItemStatus status;
}
