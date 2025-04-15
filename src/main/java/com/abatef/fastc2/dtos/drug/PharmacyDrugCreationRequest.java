package com.abatef.fastc2.dtos.drug;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDrugCreationRequest {
    @NotNull private Integer drugId;
    @NotNull private Integer pharmacyId;

    @Min(0)
    private Integer stock;

    @Min(1)
    private Float price;

    @NotNull private LocalDate expiryDate;
}
