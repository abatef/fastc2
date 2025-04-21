package com.abatef.fastc2.dtos.receipt;

import jakarta.persistence.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptCreationRequest {
    @NotNull
    private Integer pharmacyDrugId;
    @NotNull
    @Min(0)
    private Float amountDue;
    @Min(0)
    private Float discount;
    @Min(0)
    private Short units;
    @Min(0)
    private Short packs;
}
