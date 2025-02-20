package com.aminspire.domain.article.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.request.ArticleInfoRequest;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

	private final ArticleRepository articleRepository;
	private final RestTemplate restTemplate;

	// Logger 생성
	private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

	private static final String API_KEY = "pub_70553e1ca6241b5975e49ab79fa0ead9c7acf";
	private static final String NEWS_API_URL = "https://newsdata.io/api/1/news";

	// 외부 API에서 기사 검색
	public List<ArticleInfoResponse> searchArticles(String keyword, String country) {
	    try {
	        // API URL 만들기
	        String url = UriComponentsBuilder.fromUriString(NEWS_API_URL)
	                .queryParam("apikey", API_KEY)
	                .queryParam("q", keyword)
	                .queryParam("country", country)
	                .toUriString();

	        // 외부 API로부터 응답 받기
	        ArticleInfoResponse response = restTemplate.getForObject(url, ArticleInfoResponse.class);

	        if (response != null && response.getResults() != null) {
	            // 결과들을 Article 엔티티로 저장하고, ArticleInfoResponse로 변환하여 반환
	            List<Article> articles = articleRepository.saveAll(response.getResults().stream()
	                    .map(articleDto -> new Article(
	                            null, // newsId는 null로 처리 (자동 생성되므로)
	                            articleDto.getArticleId(), // articleId는 외부 API에서 제공
	                            articleDto.getTitle(),
	                            articleDto.getLink(),
	                            articleDto.getCreator(),
	                            articleDto.getDescription(),
	                            articleDto.getContent(),
	                            articleDto.getPubDate(),
	                            articleDto.getImageUrl(),
	                            articleDto.getCategory(),
	                            articleDto.getAiTag(),
	                            articleDto.getAiRegion()))
	                    .collect(Collectors.toList()));

	            // 저장된 Article 객체들을 ArticleInfoResponse 형태로 변환하여 반환
	            return articles.stream()
	                    .map(article -> ArticleInfoResponse.builder()
	                            .articleId(article.getArticleId()) // articleId를 외부 API의 ID로 설정
	                            .title(article.getTitle())
	                            .link(article.getLink())
	                            .creator(article.getCreator())
	                            .description(article.getDescription())
	                            .content(article.getContent())
	                            .pubDate(article.getPubDate())
	                            .imageUrl(article.getImageUrl())
	                            .category(article.getCategory())
	                            .aiTag(article.getAiTag())
	                            .aiRegion(article.getAiRegion())
	                            .build())
	                    .collect(Collectors.toList());
	        }

	        // 데이터가 없을 경우 빈 결과 반환
	        return List.of(ArticleInfoResponse.builder().results(Collections.emptyList()).build());
	    } catch (RestClientException e) {
	        // API 호출 중 오류가 발생한 경우
	        logger.error("API 호출 실패: " + e.getMessage(), e);
	        return List.of(ArticleInfoResponse.builder().results(Collections.emptyList()).build());
	    }
	}



	// 기사 전체 조회
	@Transactional(readOnly = true)
	public List<ArticleInfoResponse> getAllArticles() {
		List<Article> articles = articleRepository.findAll();
		if (articles == null) {
			return Collections.emptyList(); // 결과가 없으면 빈 리스트 반환
		}

		// Article 엔티티에서 필요한 데이터를 ArticleInfoResponse DTO에 맞게 매핑
		ArticleInfoResponse response = ArticleInfoResponse.builder().results(articles) // 기사 리스트 채우기
				.build();

		// 결과가 여러 개일 수 있기 때문에, List로 반환
		return List.of(response);
	}

}
