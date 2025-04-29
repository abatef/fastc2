package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.user.UserDto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDrugDto {
    @Schema(description = "Full General Drug Info", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DrugDto drug;

    @Schema(
            description = "Full General Pharmacy Info",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PharmacyDto pharmacy;

    @Schema(
            description = "Full Info of the user who added the drug to the pharmacy",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UserDto addedBy;

    @Schema(
            description = "current stock amount of the drug in the pharmacy",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer stock;

    @Schema(
            description = "the price of the drug in the pharmacy",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Float price;

    @Schema(
            description = "the expiration date of the current stock of the drug in the pharmacy",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate expiryDate;

    @Schema(
            description = "the date of adding the current stock of the drug in the pharmacy",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant createdAt;

    @Schema(
            description = "the last date the drug in the pharmacy got updated",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant updatedAt;
}
