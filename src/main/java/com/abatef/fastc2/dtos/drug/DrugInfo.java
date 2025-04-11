package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.dtos.user.UserInfo;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugInfo {
    private Integer id;
    private String name;
    private String form;
    private UserInfo createdBy;
    private Short units;
    private Float fullPrice;
    private Instant createdAt;
    private Instant updatedAt;
}
