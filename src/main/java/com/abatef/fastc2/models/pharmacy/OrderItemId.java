package com.abatef.fastc2.models.pharmacy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class OrderItemId implements Serializable {
    private static final long serialVersionUID = -6079509647907125587L;

    @NotNull
    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @NotNull
    @Column(name = "drug_id", nullable = false)
    private Integer drugId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderItemId entity = (OrderItemId) o;
        return Objects.equals(this.orderId, entity.orderId)
                && Objects.equals(this.drugId, entity.drugId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, drugId);
    }
}
