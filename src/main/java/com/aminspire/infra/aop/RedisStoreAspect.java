package com.aminspire.infra.aop;

import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.user.domain.user.User;
import com.aminspire.infra.config.redis.RedisClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedisStoreAspect {

    private static final Long EXPIRATION_TTL = 86400000L; // 24시간 TTL

    private final RedisClient redisClient;

    @Around("@annotation(com.aminspire.infra.aop.RedisStore)")
    public Object redisStoreAction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisStore redisStore = method.getAnnotation(RedisStore.class);

        String dbType = redisStore.dbType().getKey();
        String streamKey = redisStore.streamKey().getKey();
        Object[] args = joinPoint.getArgs();
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();

        log.info("[AOP] RedisStore 실행: dbType={}, streamKey={}", dbType, streamKey);

        // ✅ Scrap 저장 (ARTICLE Stream)
        if (streamKey.equals("ARTICLE") && args.length >= 2 && args[0] instanceof User && args[1] instanceof ArticleInfoResponse.ArticleInfoItems) {
            User user = (User) args[0];
            ArticleInfoResponse.ArticleInfoItems articleInfoItems = (ArticleInfoResponse.ArticleInfoItems) args[1];

            Map<String, Object> articleEvent = Map.of(
                    "articleId", articleInfoItems.getLink(),
                    "title", articleInfoItems.getTitle(),
                    "description", articleInfoItems.getDescription(),
                    "userEmail", currentUserName,
                    "timestamp", System.currentTimeMillis()
            );

            redisClient.addToStream(dbType, streamKey, articleEvent, EXPIRATION_TTL);
            log.info("[AOP] ARTICLE 스크랩 저장: {}", articleEvent);
        }
        // ✅ 검색 키워드 저장 (SEARCH Stream)
        else if (streamKey.equals("SEARCH") && args.length > 0) {
            String keyword = args[0].toString();

            Map<String, Object> searchEvent = Map.of(
                    "userEmail", currentUserName,
                    "keyword", keyword,
                    "timestamp", System.currentTimeMillis()
            );

            redisClient.addToStream(dbType, streamKey, searchEvent, EXPIRATION_TTL);
            log.info("[AOP] SEARCH 키워드 저장: {}", searchEvent);
        }

        return joinPoint.proceed();
    }
}

