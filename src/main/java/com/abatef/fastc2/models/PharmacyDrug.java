package com.abatef.fastc2.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "pharmacy_drug")
@NoArgsConstructor
public class PharmacyDrug {
    @EmbeddedId
    private PharmacyDrugId id;

    @MapsId("drugId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @MapsId("pharmacyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "added_by", nullable = false)
    private User addedBy;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "price", nullable = false)
    private Float price;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public PharmacyDrug(Drug drug, Pharmacy pharmacy, User user) {
        this.drug = drug;
        this.pharmacy = pharmacy;
        this.id.setPharmacyId(pharmacy.getId());
        this.id.setDrugId(drug.getId());
        this.addedBy = user;
    }

}