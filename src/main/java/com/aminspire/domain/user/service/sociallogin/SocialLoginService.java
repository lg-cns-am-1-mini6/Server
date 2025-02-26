package com.aminspire.domain.user.service.sociallogin;

import com.aminspire.domain.user.domain.user.LoginType;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface SocialLoginService {

    LoginResponse signInWithGoogle(String code, HttpServletResponse response);

    LoginResponse signInWithKakao(String code, HttpServletResponse response);

}
