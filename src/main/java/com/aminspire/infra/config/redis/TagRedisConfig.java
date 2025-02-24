package com.aminspire.infra.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class TagRedisConfig {

    @Bean(name = "keywordRedisTemplate")
    public StringRedisTemplate keywordRedisTemplate() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6380);
        return new StringRedisTemplate(new LettuceConnectionFactory(config));
    }
}
