package com.abatef.fastc2.dtos.receipt;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptCreationRequest {
    private Integer drugId;
    private Integer pharmacyId;
    private LocalDate drugExpiryDate;
    private Integer quantity;
    private Float amountDue;
    private Float discount;
    private Integer cashierId;
    private Integer shiftId;
    private Short units;
    private Short packs;
}
