package com.abatef.fastc2.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PharmacyDrugId implements java.io.Serializable {
    private static final long serialVersionUID = -733765486767912007L;
    @NotNull
    @Column(name = "drug_id", nullable = false)
    private Integer drugId;

    @NotNull
    @Column(name = "pharmacy_id", nullable = false)
    private Integer pharmacyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PharmacyDrugId entity = (PharmacyDrugId) o;
        return Objects.equals(this.pharmacyId, entity.pharmacyId) &&
                Objects.equals(this.drugId, entity.drugId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pharmacyId, drugId);
    }

}