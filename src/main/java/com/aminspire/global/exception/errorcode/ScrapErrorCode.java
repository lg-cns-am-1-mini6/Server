package com.aminspire.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScrapErrorCode implements BaseErrorCode {
    SCRAP_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 스크랩된 기사입니다.", "SCRAP_DUPLICATE"),

    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "스크랩된 기사가 없습니다.", "SCRAP_NOT_FOUND"),

    SCRAP_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기사 스크랩 처리 실패", "SCRAP_SAVE_FAILED");

    private final HttpStatus httpStatus;
    private final String message;
    private final String codeName;
}

