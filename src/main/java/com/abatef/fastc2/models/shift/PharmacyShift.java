package com.abatef.fastc2.models.shift;

import com.abatef.fastc2.models.pharmacy.Pharmacy;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "pharmacy_shifts")
public class PharmacyShift {
    @EmbeddedId private PharmacyShiftId id;

    @MapsId("pharmacyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @MapsId("shiftId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;
}
