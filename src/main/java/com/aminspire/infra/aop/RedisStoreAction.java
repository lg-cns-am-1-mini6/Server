package com.aminspire.infra.aop;

import com.aminspire.infra.config.redis.RedisDbTypeKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisStoreAction {
    RedisDbTypeKey redisKey();
}
