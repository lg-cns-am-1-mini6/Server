package com.aminspire.infra.config.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NaverNewsFeignConfig {

    @Value("${NAVER_CLIENT_ID}")
    private String clientId;

    @Value("${NAVER_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${NAVER_URL}")
    private String naverNewsUrl;  // 네이버 뉴스 API의 URL

    @Bean
    public RequestInterceptor naverNewsRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 네이버 뉴스 API URL에 맞는 요청만 처리
                template.target(naverNewsUrl);
                // Naver 뉴스 API에 필요한 헤더 추가
                template.header("X-Naver-Client-Id", clientId);
                template.header("X-Naver-Client-Secret", clientSecret);
            }
        };
    }
}
