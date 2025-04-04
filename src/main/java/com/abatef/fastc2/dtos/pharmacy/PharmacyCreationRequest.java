package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyCreationRequest {
    private String address;
    private Boolean isBranch = false;
    private Integer mainBranchId;
    private Location location;
}
