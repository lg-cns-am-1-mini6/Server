package com.aminspire.domain.user.service.auth;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    TokenResponse recreate(HttpServletRequest request, HttpServletResponse response);

    TokenResponse signOut(HttpServletRequest request, HttpServletResponse response);

    TokenResponse deleteUser(User user, HttpServletRequest request, HttpServletResponse response);
}
