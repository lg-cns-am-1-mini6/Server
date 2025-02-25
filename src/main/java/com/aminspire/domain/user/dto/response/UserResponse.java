package com.aminspire.domain.user.dto.response;

import lombok.Builder;

public record UserResponse(
        String email,
        String name,
        String imageUrl
) {

    public static UserResponse of(String email, String name, String imageUrl) {
        return UserResponse.builder()
                .email(email)
                .name(name)
                .imageUrl(imageUrl)
                .build();
    }

    @Builder
    public UserResponse(String email, String name, String imageUrl) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
