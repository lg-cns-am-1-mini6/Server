package com.aminspire.global.security.exception;

import com.aminspire.global.common.response.CommonResponse;
import com.aminspire.global.exception.errorcode.JwtErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(JwtErrorCode.UNAUTHORIZED.getHttpStatus().value());

        CommonResponse<String> errorResponse = CommonResponse.onFailure(
                JwtErrorCode.UNAUTHORIZED.getHttpStatus().value(), JwtErrorCode.UNAUTHORIZED.getMessage());
        String errorJson = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(errorJson);
    }
}