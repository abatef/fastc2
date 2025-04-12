package com.abatef.fastc2.models.pharmacy;

import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.Receipt;
import com.abatef.fastc2.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.*;

@Slf4j
@Getter
@Setter
@Entity
@Table(name = "pharmacy_drug")
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDrug {
    @EmbeddedId private PharmacyDrugId id;

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

    @JsonIgnore
    @OneToMany(mappedBy = "pharmacyDrug")
    private Set<Receipt> receipts = new LinkedHashSet<>();

    public PharmacyDrug(Drug drug, Pharmacy pharmacy, LocalDate expiryDate, User addedBy) {
        this.drug = drug;
        this.pharmacy = pharmacy;
        this.addedBy = addedBy;
        this.id.setExpiryDate(expiryDate);
    }
}
