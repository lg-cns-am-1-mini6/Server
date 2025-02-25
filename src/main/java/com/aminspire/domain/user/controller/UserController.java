package com.aminspire.domain.user.controller;

import com.aminspire.domain.user.dto.request.UserUpdateRequest;
import com.aminspire.domain.user.dto.response.UserResponse;
import com.aminspire.domain.user.service.user.UserService;
import com.aminspire.global.security.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 사용자 상세 정보 조회
    @GetMapping
    public UserResponse showUser(@AuthenticationPrincipal AuthDetails authDetails) {
        return userService.showUser(authDetails.user());
    }

    // 사용자 정보 수정
    @PatchMapping
    public UserResponse updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                   @AuthenticationPrincipal AuthDetails authDetails) {
        return userService.updateUser(authDetails.user(), userUpdateRequest);
    }
}
