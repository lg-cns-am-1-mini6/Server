package com.aminspire.global.security.jwt;

import com.aminspire.domain.user.domain.user.Role;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.infra.config.redis.RedisClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
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

        redisClient.setValue(user.getEmail(), refreshToken, 1000 * 60 * 60 * 24 * 7L); // Redis에 저장
    }

    public void recreate(String refreshToken, HttpServletResponse response) {
        String email = getEmail(refreshToken);
        String role = getRole(refreshToken);

        String accessToken = recreateAccessToken(email, role);
        response.setHeader("accessToken", accessToken);

        refreshToken = recreateRefreshToken(email, role);
        response.addHeader(HttpHeaders.SET_COOKIE, createResponseCookie("refreshToken", refreshToken).toString());

        redisClient.setValue(email, refreshToken, 1000 * 60 * 60 * 24 * 7L);
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

    public ResponseCookie createResponseCookie(String key, String value) {

        return ResponseCookie.from(key, value)
                .maxAge(60 * 60 * 60)
                .secure(true)
                .path("/")
                .httpOnly(true)
                .sameSite("Strict")
                .build();
    }

    public boolean validateToken(String token, String tokenType) {
        try {
            Jws<Claims> claims =
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            if (Objects.equals(tokenType, "refresh") && redisClient.checkExistsValue(token)) {
                return false;
            }

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
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

    public Long getExpirationTime(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().getTime();
    }
}
