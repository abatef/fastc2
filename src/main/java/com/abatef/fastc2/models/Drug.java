package com.abatef.fastc2.models;

import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "drugs")
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('drugs_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 250)
    @NotNull
    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "form", length = Integer.MAX_VALUE)
    private String form;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "drug")
    private Set<PharmacyDrug> pharmacyDrugs = new LinkedHashSet<>();

    @NotNull
    @ColumnDefault("0")
    @Column(name = "units", nullable = false)
    private Short units;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "full_price", nullable = false)
    private Float fullPrice;

    @JsonIgnore
    @OneToMany(mappedBy = "drug")
    private Set<Image> images = new LinkedHashSet<>();

}
