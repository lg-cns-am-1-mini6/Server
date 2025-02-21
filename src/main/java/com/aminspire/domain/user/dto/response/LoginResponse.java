package com.aminspire.domain.user.dto.response;

import lombok.Builder;

public record LoginResponse(
        String message
) {

    public static LoginResponse of(String message) {
        return LoginResponse.builder()
                .message(message)
                .build();
    }

    @Builder
    public LoginResponse(String message) {
        this.message = message;
    }
}
