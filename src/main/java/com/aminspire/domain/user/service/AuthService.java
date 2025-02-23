package com.aminspire.domain.user.service;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.response.TokenResponse;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.JwtErrorCode;
import com.aminspire.global.exception.errorcode.UserErrorCode;
import com.aminspire.global.security.jwt.JwtProvider;
import com.aminspire.infra.config.redis.RedisClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RedisClient redisClient;

    public TokenResponse recreate(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtProvider.getRefreshTokenFromCookie(request);
        boolean isValid = jwtProvider.validateToken(refreshToken, "refresh");

        if (!isValid) {
            throw new CommonException(JwtErrorCode.REFRESH_TOKEN_INVALID);
        }

        String email = jwtProvider.getEmail(refreshToken);
        String redisRefreshToken = redisClient.getValue(email);

        if (StringUtils.isEmpty(refreshToken) || StringUtils.isEmpty(redisRefreshToken) || !redisRefreshToken.equals(refreshToken)) {
            throw new CommonException(JwtErrorCode.REFRESH_TOKEN_INVALID);
        }

        jwtProvider.recreate(refreshToken, response);

        return TokenResponse.of("엑세스 토큰 재발급 성공");
    }

    @Transactional
    public TokenResponse signOut(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = jwtProvider.getAccessTokenFromRequest(request);
        String refreshToken = jwtProvider.getRefreshTokenFromCookie(request);

        if(refreshToken == null || accessToken == null){
            throw new CommonException(JwtErrorCode.TOKEN_NOT_FOUND);
        }

        if (!jwtProvider.validateToken(accessToken, "access")) {
            throw new CommonException(JwtErrorCode.ACCESS_TOKEN_INVALID);
        }

        if (!jwtProvider.validateToken(accessToken, "refresh")) {
            throw new CommonException(JwtErrorCode.REFRESH_TOKEN_INVALID);
        }

        redisClient.deleteValue(jwtProvider.getEmail(accessToken));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                .maxAge(0)
                .secure(true)
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return TokenResponse.of("로그아웃 성공");
    }
}
