package com.aminspire.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NaverErrorCode implements BaseErrorCode {
    // 400 Bad Request: 잘못된 요청 (예: 쿼리 값 없음)
    NAVER_API_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청: 검색어가 비어 있습니다.", "NAVER_API_CLIENT_ERROR"),

    // 404 Not Found: 검색 결과 없음 (네이버에서 404 던짐)
    NAVER_API_EMPTY_RESPONSE(HttpStatus.NOT_FOUND, "검색 결과가 없습니다.", "NAVER_API_EMPTY_RESPONSE"),

    // 500 Unknown Error: 예상치 못한 오류
    NAVER_API_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류", "NAVER_API_UNKNOWN_ERROR");

    private final HttpStatus httpStatus;
    private final String message;
    private final String codeName;
}
