package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.enums.UserRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyCreationRequest {
    @NotEmpty @NotNull private String name;
    @NotEmpty @NotNull private String address;
    private Boolean isBranch = false;
    private Integer mainBranchId;
    private Location location;
    private Short expiryThreshold;
}
