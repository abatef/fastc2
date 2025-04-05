package com.abatef.fastc2.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pharmacies")
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('pharmacies_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "address", nullable = false, length = Integer.MAX_VALUE)
    private String address;

    @ColumnDefault("false")
    @Column(name = "is_branch", nullable = false)
    private Boolean isBranch = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "main_branch")
    private Pharmacy mainBranch;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "pharmacy", fetch = FetchType.LAZY)
    private Set<Employee> employees = new LinkedHashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "mainBranch", fetch = FetchType.LAZY)
    private Set<Pharmacy> pharmacies = new LinkedHashSet<>();

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @JsonIgnore
    @OneToMany(mappedBy = "pharmacy")
    private Set<PharmacyDrug> pharmacyDrugs = new LinkedHashSet<>();

}