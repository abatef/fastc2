package com.abatef.fastc2.dtos.drug;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugCreationRequest {
    @NotEmpty
    @NotNull
    private String name;
    @NotNull
    @NotEmpty
    private String form;
    private Short units;
    @Min(1)
    private Float price;
}