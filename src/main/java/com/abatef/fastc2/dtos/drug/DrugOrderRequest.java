package com.abatef.fastc2.dtos.drug;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugOrderRequest {
    private Integer drugId;
    private Integer required;
}
