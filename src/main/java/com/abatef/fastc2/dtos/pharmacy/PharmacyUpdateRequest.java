package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.dtos.user.UserDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyUpdateRequest {
    @NotNull private Integer id;
    private String name;
    private UserDto owner;
    private String address;
    private Location location;
    private Short expiryThreshold;
}
