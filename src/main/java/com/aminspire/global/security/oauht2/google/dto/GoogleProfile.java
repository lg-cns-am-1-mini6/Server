package com.aminspire.global.security.oauht2.google.dto;

import lombok.Builder;

@Builder
public record GoogleProfile(
        String sub,
        String name,
        String given_name,
        String family_name,
        String picture,
        String email,
        boolean email_verified,
        String locale
) {
}