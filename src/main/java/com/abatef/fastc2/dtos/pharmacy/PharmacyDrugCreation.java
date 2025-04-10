package com.abatef.fastc2.dtos.pharmacy;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyDrugCreation {
    private Integer drugId;
    private Integer pharmacyId;
    private Integer addedByUserId;
    private Integer stock;
    private Float price;
    private LocalDate expiryDate;
}
