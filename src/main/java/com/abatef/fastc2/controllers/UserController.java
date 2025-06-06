package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.pharmacy.PharmacyDto;
import com.abatef.fastc2.dtos.user.UserDto;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Update User info")
    @PatchMapping
    public ResponseEntity<UserDto> updateUserInfo(
            @RequestBody UserDto userDto, @AuthenticationPrincipal User user) {

        UserDto updatedUser = userService.updateUserInfo(user, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete User")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get the pharmacies of the User")
    @GetMapping("/pharmacy")
    public ResponseEntity<List<PharmacyDto>> getPharmacyInfoByUser(
            @AuthenticationPrincipal User user) {
        List<PharmacyDto> info = userService.getPharmacyInfoByUser(user);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }
}
