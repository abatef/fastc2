package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import com.abatef.fastc2.dtos.user.UserInfo;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.*;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDrugInfo {
    @Schema(description = "Full General Drug Info", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DrugInfo drug;

    @Schema(
            description = "Full General Pharmacy Info",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PharmacyInfo pharmacy;

    @Schema(
            description = "Full Info of the user who added the drug to the pharmacy",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UserInfo addedBy;

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
