package com.abatef.fastc2.dtos.drug;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDrugCreationRequest {
    private Integer drugId;
    private Integer pharmacyId;
    private Integer stock;
    private Float price;
    private LocalDate expiryDate;
}
