package com.aminspire.domain.user.dto.response;

import lombok.Builder;

public record UserResponse(
        String email,
        String name
) {

    public static UserResponse of(String email, String name) {
        return UserResponse.builder()
                .email(email)
                .name(name)
                .build();
    }

    @Builder
    public UserResponse(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
