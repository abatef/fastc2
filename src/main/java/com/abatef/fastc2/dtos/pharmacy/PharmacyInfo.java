package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.dtos.user.UserInfo;

import com.abatef.fastc2.models.shift.Shift;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyInfo {
    @NotNull private Integer id;

    @Size(min = 2, max = 100)
    private String name;

    private UserInfo owner;
    private String address;
    private Location location;
    private Boolean isBranch;
    private List<Shift> shifts;
    private Short expiryThreshold;
    private Integer mainBranch;
    private Instant createdAt;
    private Instant updatedAt;
}
