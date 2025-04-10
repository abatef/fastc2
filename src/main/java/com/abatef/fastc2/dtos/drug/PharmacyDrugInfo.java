package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.dtos.user.UserInfo;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.*;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDrugInfo {
    private DrugInfo drug;
    private PharmacyInfo pharmacy;
    private UserInfo addedBy;
    private Integer stock;
    private Float price;
    private LocalDate expiryDate;
    private Instant createdAt;
    private Instant updatedAt;
}
