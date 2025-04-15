package com.abatef.fastc2.dtos.pharmacy;

import jakarta.persistence.*;
import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyDrugCreation {
    @NotNull private Integer drugId;
    @NotNull private Integer pharmacyId;
    @NotNull private Integer addedByUserId;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotNull
    @Min(0)
    private Float price;

    @NotNull private LocalDate expiryDate;
}
