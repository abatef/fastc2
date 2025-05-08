package com.abatef.fastc2.models.pharmacy;

import com.abatef.fastc2.enums.OperationStatus;
import com.abatef.fastc2.enums.OperationType;
import com.abatef.fastc2.enums.ReceiptStatus;
import com.abatef.fastc2.models.Drug;

import com.abatef.fastc2.models.User;
import jakarta.persistence.*;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "sales_operation",
        schema = "public",
        indexes = {
            @Index(
                    name = "sales_operation_idx",
                    columnList = "id, drug_id, pharmacy_id, receipt_id, order_id, type")
        })
public class SalesOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('sales_operation_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "drug_id", nullable = false)
    private Drug drug;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "order_id")
    private DrugOrder order;

    @NotNull
    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    @Enumerated(EnumType.STRING)
    private OperationType type;

    @Column(name = "amount")
    private Integer quantity;

    @Column(name = "status")
    private OperationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "cashier_id")
    private User cashier;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

}
