package com.aminspire.domain.user.dto.request;

public record UserUpdateRequest(
        String name,
        String imageUrl
) {
}
