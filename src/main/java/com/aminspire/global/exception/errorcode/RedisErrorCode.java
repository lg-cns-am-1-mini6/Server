package com.aminspire.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RedisErrorCode implements BaseErrorCode{

    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 설정에 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCodeName() {
        return this.name();
    }
}