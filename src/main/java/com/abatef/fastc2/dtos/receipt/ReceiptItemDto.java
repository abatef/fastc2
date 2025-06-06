package com.abatef.fastc2.dtos.receipt;

import com.abatef.fastc2.enums.ItemStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemDto {
    private String drugName;
    private Short units;
    private Short pack;
    private Float discount;
    private Float amountDue;
    private ItemStatus status;
}
