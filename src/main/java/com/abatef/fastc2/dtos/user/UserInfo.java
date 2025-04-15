package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Integer id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private UserRole role;
    private Boolean fbUser;
    private Boolean managedUser;
    private Instant createdAt;
    private Instant updatedAt;
}
