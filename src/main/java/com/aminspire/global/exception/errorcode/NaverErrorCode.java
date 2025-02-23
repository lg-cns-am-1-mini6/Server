package com.aminspire.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NaverErrorCode implements BaseErrorCode {
    NAVER_API_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청: 검색어가 비어 있습니다.", "NAVER_API_CLIENT_ERROR"),
    NAVER_API_EMPTY_RESPONSE(HttpStatus.NOT_FOUND, "검색 결과가 없습니다.", "NAVER_API_EMPTY_RESPONSE"),
    NAVER_API_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 서버 오류", "NAVER_API_UNKNOWN_ERROR");

    private final HttpStatus httpStatus;
    private final String message;
    private final String codeName;
}
