package com.abatef.fastc2.dtos.user;

import com.abatef.fastc2.dtos.auth.JwtAuthenticationResponse;

import com.abatef.fastc2.dtos.pharmacy.PharmacyInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationResponse {
    private UserInfo user;
    private JwtAuthenticationResponse jwt;
    private PharmacyInfo pharmacy;
}
