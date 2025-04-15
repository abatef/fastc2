package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
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
    private PharmacyInfo pharmacyDrug;
    private Integer quantity;
    private Float amountDue;
    private Float discount;
    private UserInfo cashier;
    private Instant createdAt;
    private Instant updatedAt;
}
