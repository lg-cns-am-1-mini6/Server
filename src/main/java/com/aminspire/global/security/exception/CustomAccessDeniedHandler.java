package com.aminspire.global.security.exception;

import com.aminspire.global.common.response.CommonResponse;
import com.aminspire.global.exception.ErrorMsg;
import com.aminspire.global.exception.errorcode.JwtErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(JwtErrorCode.FORBIDDEN.getHttpStatus().value());

        CommonResponse<ErrorMsg> errorResponse = CommonResponse.onFailure(
                JwtErrorCode.FORBIDDEN.getHttpStatus().value(),
                ErrorMsg.builder()
                        .code(JwtErrorCode.FORBIDDEN.getCodeName())
                        .reason(JwtErrorCode.FORBIDDEN.getMessage())
                        .build());
        String errorJson = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(errorJson);
    }
}