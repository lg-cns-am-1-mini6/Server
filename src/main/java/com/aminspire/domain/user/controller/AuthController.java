package com.aminspire.domain.user.controller;

import com.aminspire.domain.user.dto.response.LoginResponse;
import com.aminspire.domain.user.dto.response.TokenResponse;
import com.aminspire.domain.user.service.AuthServiceImpl;
import com.aminspire.domain.user.service.SocialLoginServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SocialLoginServiceImpl socialLoginServiceImpl;
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/google/sign-in")
    public LoginResponse signInWithGoogle(
            @RequestParam("code") String code, HttpServletResponse response) {
        return socialLoginServiceImpl.signInWithGoogle(code, response);
    }

    @PostMapping("/kakao/sign-in")
    public LoginResponse signInWithKakao(
            @RequestParam("code") String code, HttpServletResponse response) {
        return socialLoginServiceImpl.signInWithKakao(code, response);
    }

    @PostMapping("/reissue")
    public TokenResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        return authServiceImpl.recreate(request, response);
    }

    @PostMapping("/sign-out")
    public TokenResponse signOut(HttpServletRequest request, HttpServletResponse response) {
        return authServiceImpl.signOut(request, response);
    }
}
