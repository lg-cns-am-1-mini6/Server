package com.aminspire.infra.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.util.Map;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.db.token}")  // 0번 DB (토큰 저장용)
    private int tokenDbIndex;

    @Value("${spring.data.redis.db.keyword}") // 1번 DB (키워드 저장용)
    private int keywordDbIndex;

    // 토큰용 ConnectionFactory를 기본 빈으로 지정 (@Primary)
    @Primary
    @Bean(name = "tokenRedisConnectionFactory")
    public LettuceConnectionFactory tokenRedisConnectionFactory() {
        return createConnectionFactoryWith(tokenDbIndex);
    }

    @Bean(name = "keywordRedisConnectionFactory")
    public LettuceConnectionFactory keywordRedisConnectionFactory() {
        return createConnectionFactoryWith(keywordDbIndex);
    }

    private LettuceConnectionFactory createConnectionFactoryWith(int dbIndex) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(dbIndex);
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "tokenRedisTemplate")
    public RedisTemplate<String, Object> tokenRedisTemplate(
            @Qualifier("tokenRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }

    @Bean(name = "keywordRedisTemplate")
    public RedisTemplate<String, Object> keywordRedisTemplate(
            @Qualifier("keywordRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }

    /**
     * 기본 RedisTemplate 빈은 토큰용을 기본으로 사용
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("tokenRedisTemplate") RedisTemplate<String, Object> tokenRedisTemplate) {
        return tokenRedisTemplate;
    }

    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // Key 직렬화: String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        // Value 직렬화: JSON
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * RedisClient에서 사용할 Map<String, RedisTemplate<String, Object>> 빈을 생성
     * 키는 RedisDbTypeKey의 이름("TOKEN_KEY", "KEYWORD_KEY")로 매핑
     */
    @Bean
    public Map<String, RedisTemplate<String, Object>> redisTemplates(
            @Qualifier("tokenRedisTemplate") RedisTemplate<String, Object> tokenRedisTemplate,
            @Qualifier("keywordRedisTemplate") RedisTemplate<String, Object> keywordRedisTemplate) {
        return Map.of(
                RedisDbTypeKey.TOKEN_KEY.getKey(), tokenRedisTemplate,
                RedisDbTypeKey.KEYWORD_KEY.getKey(), keywordRedisTemplate
        );
    }
}
