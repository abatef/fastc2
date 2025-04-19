package com.abatef.fastc2.dtos.drug;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDrugCreationRequest {
    @Schema(description = "Drug ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer drugId;

    @Schema(description = "Pharmacy Id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer pharmacyId;

    @Min(0)
    @Schema(
            description = "Stock Amount",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0")
    private Integer stock;

    @Min(1)
    @Schema(
            description = "Full Price for the drug in that pharmacy",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1")
    private Float price;

    @Schema(
            description = "Expiry Date for the current stock being added",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private LocalDate expiryDate;
}
