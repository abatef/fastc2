package com.abatef.fastc2.models.pharmacy;

import com.abatef.fastc2.enums.OperationType;
import com.abatef.fastc2.models.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "operation")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('operation_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "cashier", nullable = false)
    private User cashier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @Column(name = "type", length = Integer.MAX_VALUE)
    @Enumerated(EnumType.STRING)
    private OperationType type;

    @CreationTimestamp
    @Column(name = "completed_at")
    private Instant completedAt;

}
