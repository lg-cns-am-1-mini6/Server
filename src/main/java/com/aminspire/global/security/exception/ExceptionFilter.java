package com.aminspire.global.security.exception;

import com.aminspire.global.common.response.CommonResponse;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.ErrorMsg;
import com.aminspire.global.exception.ErrorResponse;
import com.aminspire.global.exception.errorcode.JwtErrorCode;
import com.aminspire.global.security.jwt.JwtFilter;
import com.aminspire.global.security.jwt.JwtFilter.TokenInValidateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenInValidateException e) {
            setResponse(response, e);
        }
    }

    private void setResponse(HttpServletResponse response, Throwable e) throws IOException{

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(JwtErrorCode.UNAUTHORIZED.getHttpStatus().value());

        CommonResponse<String> errorResponse = CommonResponse.onFailure(
                JwtErrorCode.UNAUTHORIZED.getHttpStatus().value(), e.getMessage());
        String errorJson = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(errorJson);
    }
}