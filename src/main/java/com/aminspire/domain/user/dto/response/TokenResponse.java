package com.aminspire.domain.user.dto.response;

import lombok.Builder;

public record TokenResponse(
        String message
) {

    public static TokenResponse of(String message) {
        return TokenResponse.builder()
                .message(message)
                .build();
    }

    @Builder
    public TokenResponse(String message) {
        this.message = message;
    }
}
