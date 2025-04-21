package com.abatef.fastc2.dtos.pharmacy;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyCreationRequest {
    @Schema(description = "Pharmacy Name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @NotNull
    private String name;

    @Schema(description = "Pharmacy Address", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    @NotNull
    private String address;

    @Schema(
            description = "when creating a new branch set this to true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean isBranch = false;

    @Schema(
            description =
                    "if is branch is true the this is must be the id of the pharmacy we are creating a branch of",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer mainBranchId;

    @Schema(
            description =
                    "the location on the map of the pharmacy in form of latitude and longitude",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Location location;

    @Schema(
            description = "a global expiry threshold for the each pharmacy",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Min(0)
    private Short expiryThreshold;
}
