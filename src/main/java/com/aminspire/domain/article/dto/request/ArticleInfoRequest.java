package com.aminspire.domain.article.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleInfoRequest {

    @NotBlank(message = "검색어(query)는 필수 입력값입니다.") // null 또는 빈 문자열 방지
    private String query;

    private int display = 10; // 기본값 설정

    private int start = 1; // 기본값 설정

    private String sort = "sim"; // 기본값 설정
}
