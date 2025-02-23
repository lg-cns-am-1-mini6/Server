package com.aminspire.domain.user.controller;

import com.aminspire.domain.user.dto.response.UserResponse;
import com.aminspire.domain.user.service.UserService;
import com.aminspire.global.security.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public UserResponse showUser(@AuthenticationPrincipal AuthDetails authDetails) {
        return userService.showUser(authDetails.user());
    }
}
