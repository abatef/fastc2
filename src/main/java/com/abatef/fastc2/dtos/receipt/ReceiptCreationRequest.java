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
    private Integer pharmacyDrugId;
    private Float amountDue;
    private Float discount;
    private Short units;
    private Short packs;
}
