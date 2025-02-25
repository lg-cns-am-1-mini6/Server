package com.aminspire.domain.user.service.auth;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.domain.user.dto.response.TokenResponse;
import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.JwtErrorCode;
import com.aminspire.global.security.jwt.JwtProvider;
import com.aminspire.infra.config.redis.RedisClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtProvider jwtProvider;
    private final RedisClient redisClient;
    private final UserRepository userRepository;

    @Override
    public TokenResponse recreate(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtProvider.getRefreshTokenFromCookie(request);
        boolean isValid = jwtProvider.validateToken(refreshToken, "refresh"); // 리프레시 토큰 검증

        if (!isValid) {
            throw new CommonException(JwtErrorCode.REFRESH_TOKEN_INVALID);
        }

        String email = jwtProvider.getEmail(refreshToken);
        String redisRefreshToken = redisClient.getValue(email);

        // 요청에서 가져온 리프레시 토큰, 레디스에 저장된 리프레시 토큰 비교
        if (StringUtils.isEmpty(refreshToken) || StringUtils.isEmpty(redisRefreshToken) || !redisRefreshToken.equals(refreshToken)) {
            throw new CommonException(JwtErrorCode.REFRESH_TOKEN_INVALID);
        }

        jwtProvider.recreate(refreshToken, response); // 토큰 재발급

        return TokenResponse.of("엑세스 토큰 재발급 성공");
    }

    @Override
    @Transactional
    public TokenResponse signOut(HttpServletRequest request, HttpServletResponse response) {

        jwtProvider.invalidateTokens(request, response); // 토큰 제거

        return TokenResponse.of("로그아웃 성공");
    }

    @Override
    @Transactional
    public TokenResponse deleteUser(User user, HttpServletRequest request, HttpServletResponse response) {

        jwtProvider.invalidateTokens(request, response); // 토큰 제거

        userRepository.delete(user); // 사용자 제거

        return TokenResponse.of("사용자 탈퇴 성공");
    }
}
