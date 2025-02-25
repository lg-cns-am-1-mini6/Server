package com.aminspire.domain.user.dto.response;

import lombok.Builder;

public record PresignedUrlResponse(
        String imageUrl
) {

    public static PresignedUrlResponse of(String imageUrl) {
        return PresignedUrlResponse.builder().imageUrl(imageUrl).build();
    }

    @Builder
    public PresignedUrlResponse(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
