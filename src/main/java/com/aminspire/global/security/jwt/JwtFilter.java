package com.aminspire.global.security.jwt;

import com.aminspire.global.security.AuthDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthDetailsService authDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtProvider.getAccessTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtProvider.validateToken(token, "access")) {
            String email = jwtProvider.getEmail(token);
            UserDetails userDetails = authDetailsService.loadUserByUsername(email);

            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    public static class TokenInValidateException extends JwtException {

        public TokenInValidateException(String message) {
            super(message);
        }

        public TokenInValidateException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
