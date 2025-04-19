package com.abatef.fastc2.dtos.drug;

import io.swagger.v3.oas.annotations.media.Schema;

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

    @Schema(description = "Drug Name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @NotNull
    private String name;

    @Schema(
            description = "Drug Form (e.g. Tablet, Syrup, etc.)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @NotEmpty
    private String form;

    @Schema(
            description = "Number of Units of the drug (e.g Number of Sachets)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Short units;

    @Min(1)
    @Schema(
            description = "The full price of the drug",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1")
    private Float price;
}
