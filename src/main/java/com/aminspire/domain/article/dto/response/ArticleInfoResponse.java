package com.aminspire.domain.article.dto.response;

import java.util.List;

import com.aminspire.domain.article.domain.Article;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스 API 응답을 담는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleInfoResponse {
	private List<Article> results; // 기사 리스트
	
    @JsonProperty("article_id")
    private String articleId;  // 외부 API에서 받은 ID

    private String title;
    private String link;

    private List<String> creator;  // 작성자 리스트

    private String description;
    private String content;

    @JsonProperty("pubDate")
    private String pubDate;

    @JsonProperty("image_url")
    private String imageUrl;

    private List<String> category;  // 여러 카테고리를 리스트로 저장

    @JsonProperty("ai_tag")
    private List<String> aiTag;  // AI 태그 리스트

    @JsonProperty("ai_region")
    private String aiRegion;  // AI 예측 지역 정보
}
