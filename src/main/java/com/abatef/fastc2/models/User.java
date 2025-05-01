package com.abatef.fastc2.models;

import com.abatef.fastc2.enums.UserRole;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "phone", length = Integer.MAX_VALUE)
    private String phone;

    @ColumnDefault("false")
    @Column(name = "fb_user")
    private Boolean fbUser;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Pharmacy> pharmacies = new LinkedHashSet<>();

    @JsonIgnore
    @Column(name = "password", length = Integer.MAX_VALUE)
    private String password;

    @JsonIgnore
    @Column(name = "fb_uid", length = Integer.MAX_VALUE)
    private String fbUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy")
    private Set<Drug> drugs = new LinkedHashSet<>();

    @JsonIgnore @OneToMany private Set<Image> images = new LinkedHashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "addedBy")
    private Set<PharmacyDrug> pharmacyDrugs = new LinkedHashSet<>();

    @ColumnDefault("false")
    @Column(name = "managed_user")
    private Boolean managedUser;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private Employee employee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return username;
    }
}
