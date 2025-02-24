package com.aminspire.infra.config.redis;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RedisClusterKey {
    TOKEN_KEY("TOKEN"),
    KEYWORD_KEY("KEYWORD");

    private final String key;
}
