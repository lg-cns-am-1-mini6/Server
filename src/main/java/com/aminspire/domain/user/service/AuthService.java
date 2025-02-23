package com.aminspire.domain.user.service;

import com.aminspire.domain.user.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    TokenResponse recreate(HttpServletRequest request, HttpServletResponse response);

    TokenResponse signOut(HttpServletRequest request, HttpServletResponse response);
}
