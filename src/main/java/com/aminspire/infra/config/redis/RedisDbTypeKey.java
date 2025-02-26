package com.aminspire.infra.config.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum RedisDbTypeKey {
    TOKEN_KEY("TOKEN"),
    KEYWORD_KEY("KEYWORD");

    private final String key;
}
