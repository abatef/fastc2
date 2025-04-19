package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.dtos.user.UserInfo;
import com.abatef.fastc2.models.shift.Shift;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PharmacyUpdateRequest {
    private Integer id;
    private String name;
    private UserInfo owner;
    private String address;
    private Location location;
    private Short expiryThreshold;
}
