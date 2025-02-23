package com.aminspire.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScrapErrorCode implements BaseErrorCode {
    // 400 Bad Request: 잘못된 요청 (예: 쿼리 값 없음)
    SCRAP_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청: 검색어가 비어 있습니다.", "SCRAP_CLIENT_ERROR"),

    // 404 Not Found: 검색 결과 없음
    SCRAP_EMPTY_RESPONSE(HttpStatus.NOT_FOUND, "검색 결과가 없습니다.", "SCRAP_EMPTY_RESPONSE"),

    // 500 Unknown Error: 예상치 못한 오류
    SCRAP_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류", "SCRAP_UNKNOWN_ERROR"),

    SCRAP_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 스크랩된 기사입니다.", "SCRAP_DUPLICATE"),

    SCRAP_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기사 스크랩 저장 실패", "SCRAP_SAVE_FAILED"),

    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 스크랩을 찾을 수 없습니다.", "SCRAP_NOT_FOUND");

    private final HttpStatus httpStatus;
    private final String message;
    private final String codeName;
}

