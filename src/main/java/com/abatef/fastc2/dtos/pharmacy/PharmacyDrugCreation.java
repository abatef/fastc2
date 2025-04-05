package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.models.Drug;
import com.abatef.fastc2.models.Pharmacy;
import com.abatef.fastc2.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyDrugCreation {
    private Integer drugId;
    private Integer pharmacyId;
    private Integer addedByUserId;
    private Integer stock;
    private Float price;
}
