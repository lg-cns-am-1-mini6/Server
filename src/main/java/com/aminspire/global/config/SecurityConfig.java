package com.aminspire.global.config;

import com.aminspire.global.security.exception.CustomAccessDeniedHandler;
import com.aminspire.global.security.exception.CustomAuthenticationEntryPoint;
import com.aminspire.global.security.exception.ExceptionFilter;
import com.aminspire.global.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ExceptionFilter exceptionFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
/*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            web.ignoring()
                    .requestMatchers("/articles/search");// 필터를 타면 안되는 경로
        };
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable);

        http.sessionManagement(
                (session) ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS));
        http.httpBasic(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable);

        http.exceptionHandling((exceptionHandling) ->
                exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler) // 인가 예외 처리
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증 예외 처리
        ); // 에러 핸들러 등록

        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/auth/google/sign-in", "auth/kakao/sign-in", "/auth/reissue").permitAll()
                        .requestMatchers("/articles/search").permitAll()
                        .requestMatchers("/auth/sign-out", "/auth/cancel", "/user/**").authenticated()
                        .anyRequest().permitAll()) // 인가 경로 설정
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionFilter, JwtFilter.class); // JwtFilter 내부 예외 처리 필터 등록

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "https://newjeans.site/"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        config.setExposedHeaders(Collections.singletonList("Set-Cookie"));
        return source;
    }
}
