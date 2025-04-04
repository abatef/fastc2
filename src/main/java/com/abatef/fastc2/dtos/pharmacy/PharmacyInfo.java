package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.models.Employee;
import com.abatef.fastc2.models.Pharmacy;
import com.abatef.fastc2.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyInfo {
    private Integer id;
    private Integer owner;
    private String address;
    private Location location;
    private Boolean isBranch;
    private Integer mainBranch;
    private Instant createdAt;
    private Instant updatedAt;
}
