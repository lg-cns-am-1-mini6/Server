package com.aminspire.global.security.oauht2.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoProfile(
        Boolean set_privacy_info,
        Long id,
        String connected_at,
        Properties properties,
        KakaoAccount kakao_account) {
    public record Properties(String nickname) {}

    public record KakaoAccount(
            Boolean profile_nickname_needs_agreement,
            Profile profile,
            Boolean has_email,
            Boolean email_needs_agreement,
            Boolean is_email_valid,
            Boolean is_email_verified,
            String email,
            Boolean gender_needs_agreement,
            Boolean has_gender,
            String gender) {
        public record Profile(String nickname, Boolean is_default_nickname) {}
    }
}
