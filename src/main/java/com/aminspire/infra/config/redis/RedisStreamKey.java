package com.aminspire.infra.config.redis;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RedisStreamKey {
    SEARCH_STREAM_KEY("SEARCH"),
    ARTICLE_STREAM_KEY("ARTICLE");

    private final String key;


}
