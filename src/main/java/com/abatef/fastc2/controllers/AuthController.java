package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.auth.JwtAuthenticationRequest;
import com.abatef.fastc2.dtos.auth.JwtAuthenticationResponse;
import com.abatef.fastc2.dtos.auth.RefreshTokenRequest;
import com.abatef.fastc2.dtos.user.UserCreationRequest;
import com.abatef.fastc2.dtos.user.UserCreationResponse;
import com.abatef.fastc2.dtos.user.UserInfo;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.security.auth.RefreshToken;
import com.abatef.fastc2.security.auth.RefreshTokenService;
import com.abatef.fastc2.services.PharmacyService;
import com.abatef.fastc2.services.UserService;
import com.abatef.fastc2.utils.JwtUtil;

import jakarta.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    private final JwtUtil jwtUtil;
    private final PharmacyService pharmacyService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService,
            UserService userService,
            ModelMapper modelMapper,
            JwtUtil jwtUtil, PharmacyService pharmacyService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.pharmacyService = pharmacyService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserCreationResponse> signup(
            @Valid @RequestBody UserCreationRequest userCreationRequest) {
        User user = userService.registerUser(userCreationRequest);
        UserInfo userInfo = modelMapper.map(user, UserInfo.class);
        JwtAuthenticationRequest request =
                new JwtAuthenticationRequest(user.getUsername(), userCreationRequest.getPassword());
        JwtAuthenticationResponse jwtResponse = login(request).getBody();
        return ResponseEntity.ok(new UserCreationResponse(userInfo, jwtResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(
            @Valid @RequestBody JwtAuthenticationRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtil.generateAccessToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String refreshToken = refreshTokenService.generateRefreshToken(userDetails).getToken();
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        RefreshToken validToken = refreshTokenService.validateRefreshToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(validToken.getUsername());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = refreshTokenService.generateRefreshToken(userDetails).getToken();
        return ResponseEntity.ok(new JwtAuthenticationResponse(newAccessToken, newRefreshToken));
    }

    @PostMapping("/password")
    public ResponseEntity<JwtAuthenticationResponse> updatePassword(
            @RequestBody Map<String, Object> body, @AuthenticationPrincipal User user) {
        User updatedUser = userService.updateUserPassword(user, (String) body.get("password"));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        JwtAuthenticationRequest request =
                new JwtAuthenticationRequest(user.getUsername(), (String) body.get("password"));
        return login(request);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(modelMapper.map(user, UserInfo.class));
    }
}
