package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugOrderDto {
    private DrugDto drug;
    private PharmacyDto pharmacy;
    private Integer requiredAverage;
    private Integer nOrders;
}
