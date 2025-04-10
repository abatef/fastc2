package com.abatef.fastc2.dtos.drug;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugCreationRequest {
    private String name;
    private String form;
    private Short units;
    private Float price;
}
