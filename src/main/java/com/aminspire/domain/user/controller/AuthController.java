package com.aminspire.domain.user.controller;

import com.aminspire.domain.user.dto.response.LoginResponse;
import com.aminspire.domain.user.dto.response.TokenResponse;
import com.aminspire.domain.user.service.auth.AuthService;
import com.aminspire.domain.user.service.sociallogin.SocialLoginService;
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

    // 구글 로그인
    @PostMapping("/google/sign-in")
    public LoginResponse signInWithGoogle(
            @RequestParam("code") String code, HttpServletResponse response) {
        return socialLoginService.signInWithGoogle(code, response);
    }

    // 카카오 로그인
    @PostMapping("/kakao/sign-in")
    public LoginResponse signInWithKakao(
            @RequestParam("code") String code, HttpServletResponse response) {
        return socialLoginService.signInWithKakao(code, response);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public TokenResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        return authService.recreate(request, response);
    }

    // 로그아웃
    @PostMapping("/sign-out")
    public TokenResponse signOut(HttpServletRequest request, HttpServletResponse response) {
        return authService.signOut(request, response);
    }

    // 사용자 탈퇴
    @DeleteMapping("/cancel")
    public TokenResponse deleteUser(HttpServletRequest request, HttpServletResponse response,
                                    @AuthenticationPrincipal AuthDetails authDetails) {
        return authService.deleteUser(authDetails.user(), request, response);
    }
}
