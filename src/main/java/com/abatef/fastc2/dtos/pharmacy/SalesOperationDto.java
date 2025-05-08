package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.dtos.drug.DrugDto;
import com.abatef.fastc2.dtos.drug.DrugOrderDto;
import com.abatef.fastc2.dtos.receipt.ReceiptDto;
import com.abatef.fastc2.enums.OperationStatus;
import com.abatef.fastc2.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesOperationDto {
    private DrugDto drug;
    private ReceiptDto receipt;
    private DrugOrderDto order;
    private PharmacyDto pharmacy;
    private Integer quantity;
    private OperationType type;
    private OperationStatus status;
}
