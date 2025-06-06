package com.abatef.fastc2.dtos.drug;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugUpdateRequest {
    @Schema(description = "Drug Id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull
    private Integer id;

    @Schema(description = "Drug Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 1, max = 255)
    private String name;

    @Schema(
            description = "Drug Form (e.g. Tablet, Syrup, etc.)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(min = 1, max = 255)
    private String form;

    @Schema(
            description = "Number of Units of the drug (e.g Number of Sachets)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(0)
    private Short units;

    @Schema(
            description = "The full price of the drug",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(1)
    private Float fullPrice;
}
