package com.aminspire.domain.article.domain;

import com.aminspire.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "article") // 일반적으로 테이블명은 소문자로 설정
public class Article extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long newsId;

	@Column(name = "article_id", length = 255, unique = true) // 길이 제한 및 유니크 설정
	private String articleId; // 외부 API에서 받은 ID를 사용

	@Column(name = "title")
	private String title;
	
	@Column(name = "link")
	private String link;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "article_creators", joinColumns = @JoinColumn(name = "article_id"))
	@Column(name = "creator")
	private List<String> creator; // 작성자가 여러 명일 수 있음

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "pub_date")
	private String pubDate;

	@Column(name = "image_url")
	private String imageUrl;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "article_categories", joinColumns = @JoinColumn(name = "article_id"))
	@Column(name = "category")
	private List<String> category; // 여러 개의 카테고리를 가질 수 있음

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "article_ai_tags", joinColumns = @JoinColumn(name = "article_id"))
	@Column(name = "ai_tag")
	private List<String> aiTag; // 여러 개의 AI 태그 저장 가능

	@Column(name = "ai_region")
	private String aiRegion; // AI 예측 지역 정보
}