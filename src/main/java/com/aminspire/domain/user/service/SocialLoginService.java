package com.aminspire.domain.user.service;

import com.aminspire.domain.user.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface SocialLoginService {

    LoginResponse signInWithGoogle(String code, HttpServletResponse response);

    LoginResponse signInWithKakao(String code, HttpServletResponse response);
}
