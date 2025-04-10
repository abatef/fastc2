package com.abatef.fastc2.dtos.pharmacy;

import com.abatef.fastc2.dtos.user.UserInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyInfo {
    private Integer id;
    private String name;
    private UserInfo owner;
    private String address;
    private Location location;
    private Boolean isBranch;
    private Integer mainBranch;
    private Instant createdAt;
    private Instant updatedAt;
}
