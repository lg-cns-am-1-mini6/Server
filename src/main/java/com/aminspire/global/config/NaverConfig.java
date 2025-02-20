package com.aminspire.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "naver.client")
public class NaverConfig {
    private String id;
    private String secret;
}
