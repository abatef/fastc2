package com.abatef.fastc2.models.pharmacy;

import com.abatef.fastc2.models.Drug;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "drug_order")
public class DrugOrder {
    @EmbeddedId private DrugOrderId id;

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

    @ColumnDefault("0")
    @Column(name = "required")
    private Integer required;

    @ColumnDefault("0")
    @Column(name = "n_orders")
    private Integer nOrders;
}
