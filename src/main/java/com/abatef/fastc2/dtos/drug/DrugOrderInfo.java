package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugOrderInfo {
    private DrugInfo drug;
    private PharmacyInfo pharmacy;
    private Integer requiredAverage;
    private Integer nOrders;
}
