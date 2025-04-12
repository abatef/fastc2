package com.abatef.fastc2.models.shift;

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
public class PharmacyShiftId implements java.io.Serializable {
    private static final long serialVersionUID = 3797661422918986429L;

    @NotNull
    @Column(name = "pharmacy_id", nullable = false)
    private Integer pharmacyId;

    @NotNull
    @Column(name = "shift_id", nullable = false)
    private Integer shiftId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PharmacyShiftId entity = (PharmacyShiftId) o;
        return Objects.equals(this.shiftId, entity.shiftId)
                && Objects.equals(this.pharmacyId, entity.pharmacyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shiftId, pharmacyId);
    }
}
