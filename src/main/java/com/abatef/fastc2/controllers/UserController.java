package com.abatef.fastc2.controllers;

import com.abatef.fastc2.dtos.user.UserInfo;
import com.abatef.fastc2.models.User;
import com.abatef.fastc2.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(modelMapper.map(user, UserInfo.class));
    }


}
