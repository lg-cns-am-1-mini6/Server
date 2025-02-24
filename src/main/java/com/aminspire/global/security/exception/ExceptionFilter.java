package com.aminspire.global.security.exception;

import com.aminspire.global.common.response.CommonResponse;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.ErrorMsg;
import com.aminspire.global.exception.ErrorResponse;
import com.aminspire.global.exception.errorcode.JwtErrorCode;
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
        } catch (CommonException e) {
            setResponse(response);
        }
    }

    private void setResponse(HttpServletResponse response) throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(JwtErrorCode.ACCESS_TOKEN_INVALID.getHttpStatus().value());

        CommonResponse<String> errorResponse = CommonResponse.onFailure(
                JwtErrorCode.ACCESS_TOKEN_INVALID.getHttpStatus().value(), JwtErrorCode.ACCESS_TOKEN_INVALID.getMessage());
        String errorJson = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(errorJson);
    }
}