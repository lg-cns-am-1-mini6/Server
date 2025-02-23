package com.aminspire.global.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
    USER_INVALID_ROLE(HttpStatus.FORBIDDEN, "로그인한 유저만 처리할 수 있습니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "허용 되지 않은 회원입니다.");// FORBIDDEN(403)으로 변경

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCodeName() {
        return this.name();
    }
}
