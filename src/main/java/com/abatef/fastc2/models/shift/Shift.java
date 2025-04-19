package com.abatef.fastc2.models.shift;

import com.abatef.fastc2.models.Employee;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ColumnDefault;

import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('shifts_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "shift", fetch = FetchType.EAGER)
    private Set<Employee> employees = new LinkedHashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pharmacy_shifts",
            joinColumns = @JoinColumn(name = "shift_id"),
            inverseJoinColumns = @JoinColumn(name = "pharmacy_id"))
    private Set<Pharmacy> pharmacies = new LinkedHashSet<>();
}
