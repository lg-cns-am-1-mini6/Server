package com.aminspire.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtErrorCode implements BaseErrorCode{

    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유호하지 않은 리프레시 토큰입니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT 토큰이 존재하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "사용자 인증에 실패했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCodeName() {
        return this.name();
    }
}
