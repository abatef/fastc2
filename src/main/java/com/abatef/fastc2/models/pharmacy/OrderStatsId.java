package com.abatef.fastc2.models.pharmacy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatsId implements Serializable {
    private static final long serialVersionUID = 846980771570541242L;

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
        OrderStatsId entity = (OrderStatsId) o;
        return Objects.equals(this.pharmacyId, entity.pharmacyId)
                && Objects.equals(this.drugId, entity.drugId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pharmacyId, drugId);
    }
}
