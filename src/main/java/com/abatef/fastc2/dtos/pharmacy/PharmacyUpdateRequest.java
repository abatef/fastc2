package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.dtos.user.UserInfo;
import com.abatef.fastc2.models.shift.Shift;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
