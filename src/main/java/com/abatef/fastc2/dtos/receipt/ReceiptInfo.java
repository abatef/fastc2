package com.abatef.fastc2.dtos.receipt;

import com.abatef.fastc2.models.shift.Shift;
import com.abatef.fastc2.dtos.drug.PharmacyDrugInfo;
import com.abatef.fastc2.dtos.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptInfo {
    private Integer id;
    private PharmacyDrugInfo pharmacyDrug;
    private Integer quantity;
    private Float amountDue;
    private Float discount;
    private Short units;
    private Short packs;
    private UserInfo cashier;
    private Shift shift;
    private Instant createdAt;
    private Instant updatedAt;
}
