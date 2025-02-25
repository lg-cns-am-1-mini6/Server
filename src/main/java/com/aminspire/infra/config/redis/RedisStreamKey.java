package com.aminspire.infra.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum RedisStreamKey {
    SEARCH_STREAM_KEY("SEARCH"),
    ARTICLE_STREAM_KEY("ARTICLE");

    private final String key;


}
