package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.dtos.user.UserInfo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyUpdateRequest {
    @NotNull
    private Integer id;
    @Min(1)
    private String name;
    private UserInfo owner;
    @Min(1)
    private String address;
    private Location location;
    @Min(0)
    private Short expiryThreshold;
}
