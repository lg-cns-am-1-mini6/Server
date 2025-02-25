package com.aminspire.infra.aop;

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

    private final RedisClient redisClient;
    @Around("@annotation(RedisStoreAction)")
    public Object redisStoreAction(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedisStoreAction redisStoreAction = method.getAnnotation(RedisStoreAction.class);

        String redisKey = redisStoreAction.redisKey().name();
        Object[] args = joinPoint.getArgs();
        String key = args.length > 0 ? args[0].toString() : "unknown";
        Long currentUser = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        Map<String, Object> searchEvent = Map.of(
                "userId", currentUser,
                "keyword", key,
                "timestamp", System.currentTimeMillis()
        );

        redisClient.addToStream(redisKey, "search_stream", searchEvent);

        log.info("[AOP] Redis Streams 저장: redisKey={}, key={}, event={}", redisKey, key, searchEvent);

        return joinPoint.proceed();
    }
}
