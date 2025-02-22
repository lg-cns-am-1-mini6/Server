package com.aminspire.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NaverApiErrorCode implements BaseErrorCode {
    NAVER_API_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "네이버 API 요청 오류 (4xx)", "NAVER_API_CLIENT_ERROR"),
    NAVER_API_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 서버 오류 (5xx)", "NAVER_API_SERVER_ERROR"),
    NAVER_API_UNKNOWN_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 알 수 없는 오류", "NAVER_API_UNKNOWN_ERROR"),
    NAVER_API_JSON_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파싱 오류", "NAVER_API_JSON_ERROR"),
    NAVER_API_EMPTY_RESPONSE(
            HttpStatus.NO_CONTENT, "네이버 API 응답이 비어 있음", "NAVER_API_EMPTY_RESPONSE");

    private final HttpStatus httpStatus;
    private final String message;
    private final String codeName;
}
