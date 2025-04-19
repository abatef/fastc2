package com.abatef.fastc2.dtos.receipt;

import com.abatef.fastc2.models.pharmacy.PharmacyDrug;
import com.abatef.fastc2.models.pharmacy.Receipt;
import com.abatef.fastc2.models.shift.Shift;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemInfo {
    private String drugName;
    private Short units;
    private Short pack;
    private Float discount;
    private Float amountDue;
    private Shift shift;
}
