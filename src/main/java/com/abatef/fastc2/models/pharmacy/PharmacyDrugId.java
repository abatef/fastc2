package com.abatef.fastc2.models.pharmacy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyDrugId implements java.io.Serializable {
    private static final long serialVersionUID = 6355921215473082425L;

    @NotNull
    @Column(name = "drug_id", nullable = false)
    private Integer drugId;

    @NotNull
    @Column(name = "pharmacy_id", nullable = false)
    private Integer pharmacyId;

    @NotNull
    @ColumnDefault("(CURRENT_DATE + 100)")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PharmacyDrugId entity = (PharmacyDrugId) o;
        return Objects.equals(this.expiryDate, entity.expiryDate)
                && Objects.equals(this.pharmacyId, entity.pharmacyId)
                && Objects.equals(this.drugId, entity.drugId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expiryDate, pharmacyId, drugId);
    }
}
