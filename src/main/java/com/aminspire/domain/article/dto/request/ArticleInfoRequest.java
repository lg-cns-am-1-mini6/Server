package com.aminspire.domain.article.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트에서 뉴스 검색 요청 시 사용하는 DTO
 * 사용자가 선택한 검색어(keyword)와 국가(country)를 포함함
 * 카테고리, 언어는 지정 X. 기간은 지난 48시간만 허용.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleInfoRequest {

    @NotBlank(message = "검색어(keyword)는 필수 입력값입니다.") // null 또는 빈 문자열 방지
    private String keyword;

    @Builder.Default
    @Pattern(regexp = "^[a-z]{2}(,[a-z]{2})*$", message = "올바른 국가 코드를 입력하세요. (예: kr,us)")
    private String country = "kr,us";
}
