package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.pharmacy.Pharmacy;
import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDrugShortage {
    private DrugInfo drug;
    private PharmacyInfo pharmacy;
    private Integer shortage;
}
