package com.aminspire.global.security.jwt;

import com.aminspire.domain.user.domain.user.User;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.JwtErrorCode;
import com.aminspire.global.security.jwt.JwtFilter.TokenInValidateException;
import com.aminspire.infra.config.redis.RedisClient;
import com.aminspire.infra.config.redis.RedisDbTypeKey;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @Value("${jwt.token.access-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshTokenExpirationTime;

    private final RedisClient redisClient;

    @PostConstruct
    protected void init() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public String createAccessToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String createRefreshToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void createToken(User user, HttpServletResponse response) {
        String accessToken = createAccessToken(user);
        response.setHeader("accessToken", accessToken); // Access 토큰: 로컬 스토리지에 저장

        String refreshToken = createRefreshToken(user);
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                createResponseCookie("refreshToken", refreshToken)
                        .toString()); // Refresh 토큰: 쿠키에 저장
        log.info("accessToken: "+accessToken);
        log.info("refreshToken: "+refreshToken);
        redisClient.setValue(RedisDbTypeKey.TOKEN_KEY.getKey(), user.getEmail(), refreshToken, 1000 * 60 * 60 * 24 * 7L); // Redis에 저장
    }

    // 엑세스 토큰 및 리프레시 토큰 재발급
    public void recreate(String refreshToken, HttpServletResponse response) {
        String email = getEmail(refreshToken);
        String role = getRole(refreshToken);

        String accessToken = recreateAccessToken(email, role);
        response.setHeader("accessToken", accessToken);

        refreshToken = recreateRefreshToken(email, role);
        response.addHeader(HttpHeaders.SET_COOKIE, createResponseCookie("refreshToken", refreshToken).toString());

        redisClient.setValue(RedisDbTypeKey.TOKEN_KEY.getKey(), email, refreshToken, 1000 * 60 * 60 * 24 * 7L); // Redis 값 덮어쓰기
    }

    public String recreateAccessToken(String email, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String recreateRefreshToken(String email, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 쿠키 생성
    public ResponseCookie createResponseCookie(String key, String value) {

        return ResponseCookie.from(key, value)
                .maxAge(60 * 60 * 60)
                .secure(true)
                .path("/")
                .httpOnly(true)
                .sameSite("None")
                .build();
    }

    // JWT 토큰 검증
    public boolean validateToken(String token, String tokenType) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            if (Objects.equals(tokenType, "refresh") && redisClient.checkExistsValue(RedisDbTypeKey.TOKEN_KEY.getKey(),token)) {
                return false;
            }

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new TokenInValidateException("잘못된 JWT 서명입니다.", e);

        } catch (ExpiredJwtException e) {
            throw new TokenInValidateException("만료된 JWT 토큰입니다.", e);

        } catch (UnsupportedJwtException e) {
            throw new TokenInValidateException("지원되지 않는 JWT 토큰입니다.", e);

        } catch (IllegalArgumentException e) {
            throw new TokenInValidateException("JWT 토큰이 잘못되었습니다.", e);
        }
    }

    // 엑세스 토큰 및 리프레시 토큰 제거
    public void invalidateTokens(HttpServletRequest request, HttpServletResponse response) {

        String accessToken = getAccessTokenFromRequest(request);
        String refreshToken = getRefreshTokenFromCookie(request);

        if(refreshToken == null || accessToken == null){
            throw new CommonException(JwtErrorCode.TOKEN_NOT_FOUND);
        }

        if (!validateToken(accessToken, "access")) {
            throw new CommonException(JwtErrorCode.ACCESS_TOKEN_INVALID);
        }

        if (!validateToken(accessToken, "refresh")) {
            throw new CommonException(JwtErrorCode.REFRESH_TOKEN_INVALID);
        }

        redisClient.deleteValue(RedisDbTypeKey.TOKEN_KEY.getKey(),getEmail(accessToken)); // Redis에서 리프레시 토큰 제거

        // 쿠키 제거
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                .maxAge(0)
                .secure(true)
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    // 헤더로부터 엑세스 토큰 추출
    public String getAccessTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    // 쿠키로부터 리프레시 토큰 추출
    public String getRefreshTokenFromCookie(HttpServletRequest request) {

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        return refreshToken;
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }
}
