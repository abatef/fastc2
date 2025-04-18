package com.abatef.fastc2.dtos.drug;

import com.abatef.fastc2.dtos.user.UserInfo;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugInfo {
    @Schema(description = "Drug Id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer id;

    @Schema(description = "Drug Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    @Schema(
            description = "Drug Form (e.g. Tablet, Syrup, etc.)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String form;

    @Schema(
            description = "The User Who Created The Drug",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UserInfo createdBy;

    @Schema(
            description = "Number of Units of the drug (e.g Number of Sachets)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Short units;

    @Schema(
            description = "The full price of the drug",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Float fullPrice;

    @Schema(
            description = "The Date Which The Drug was Created At.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant createdAt;

    @Schema(
            description = "The Date of The Last Drug Update",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Instant updatedAt;
}
