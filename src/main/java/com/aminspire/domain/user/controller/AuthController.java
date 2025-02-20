package com.aminspire.domain.user.controller;

import com.aminspire.domain.user.service.SocialLoginService;
import com.aminspire.global.common.response.CommonResponse;
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

    private final SocialLoginService socialLoginService;

    @PostMapping("/google/sign-in")
    public CommonResponse<?> signInWithGoogle(@RequestParam("code") String code, HttpServletResponse response) {
        socialLoginService.signInWithGoogle(code, response);
        return CommonResponse.onSuccess(200, "구글 로그인 성공");
    }
}
