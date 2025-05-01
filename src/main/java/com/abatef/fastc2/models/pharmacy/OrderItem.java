package com.abatef.fastc2.models.pharmacy;

import com.abatef.fastc2.models.Drug;

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
@Table(
        name = "order_item",
        schema = "public",
        indexes = {@Index(name = "order_item_idx", columnList = "order_id, drug_id, required")})
public class OrderItem {
    @EmbeddedId private OrderItemId id;

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "order_id", nullable = false)
    private DrugOrder order;

    @MapsId("drugId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "required", nullable = false)
    private Integer required;
}
