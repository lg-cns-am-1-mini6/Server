package com.aminspire.domain.user.controller;

import com.aminspire.domain.user.dto.response.LoginResponse;
import com.aminspire.domain.user.dto.response.TokenResponse;
import com.aminspire.domain.user.service.AuthService;
import com.aminspire.domain.user.service.SocialLoginService;
import com.aminspire.global.security.AuthDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SocialLoginService socialLoginService;
    private final AuthService authService;

    @PostMapping("/google/sign-in")
    public LoginResponse signInWithGoogle(
            @RequestParam("code") String code, HttpServletResponse response) {
        return socialLoginService.signInWithGoogle(code, response);
    }

    @PostMapping("/kakao/sign-in")
    public LoginResponse signInWithKakao(
            @RequestParam("code") String code, HttpServletResponse response) {
        return socialLoginService.signInWithKakao(code, response);
    }

    @PostMapping("/reissue")
    public TokenResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        return authService.recreate(request, response);
    }

    @PostMapping("/sign-out")
    public TokenResponse signOut(HttpServletRequest request, HttpServletResponse response) {
        return authService.signOut(request, response);
    }

    @DeleteMapping("/cancel")
    public TokenResponse deleteUser(HttpServletRequest request, HttpServletResponse response,
                                    @AuthenticationPrincipal AuthDetails authDetails) {
        return authService.deleteUser(authDetails.user(), request, response);
    }
}
