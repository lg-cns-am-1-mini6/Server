package com.aminspire.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NaverApiErrorCode implements BaseErrorCode {
    // 400 Bad Request: 잘못된 요청 (예: 쿼리 값 없음)
    NAVER_API_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청: 검색어가 비어 있습니다.", "NAVER_API_CLIENT_ERROR"),

    // 204 No Content: 검색 결과 없음
    NAVER_API_EMPTY_RESPONSE(HttpStatus.NO_CONTENT, "검색 결과가 없습니다.", "NAVER_API_EMPTY_RESPONSE"),

    // 500 Internal Server Error: 네이버 API 서버 오류
    NAVER_API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 서버 오류", "NAVER_API_SERVER_ERROR"),

    // 500 Internal Server Error: JSON 파싱 오류 (5xx에 포함됨)
    NAVER_API_JSON_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파싱 오류", "NAVER_API_JSON_ERROR"),

    // 520 Unknown Error: 예상치 못한 오류
    NAVER_API_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류", "NAVER_API_UNKNOWN_ERROR");


    private final HttpStatus httpStatus;
    private final String message;
    private final String codeName;
}
