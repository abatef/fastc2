package com.abatef.fastc2.models.pharmacy;

import com.abatef.fastc2.enums.ReceiptStatus;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.models.shift.Shift;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "receipt")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('receipt_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "cashier", nullable = false)
    private User cashier;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "receipt", fetch = FetchType.EAGER)
    private Set<ReceiptItem> receiptItems = new LinkedHashSet<>();

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;
}
