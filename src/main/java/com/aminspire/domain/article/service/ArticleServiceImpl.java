package com.aminspire.domain.article.service;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.global.config.NaverConfig;
import com.aminspire.global.exception.CommonException;
import com.aminspire.global.exception.errorcode.NaverApiErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

	private final RestTemplate restTemplate;
	private final NaverConfig naverConfig;
	private final ObjectMapper objectMapper;

	@Autowired
	public ArticleServiceImpl(RestTemplate restTemplate, NaverConfig naverConfig, ObjectMapper objectMapper) {
		this.restTemplate = restTemplate;
		this.naverConfig = naverConfig;
		this.objectMapper = objectMapper;
	}

	@Override
	public List<ArticleInfoResponse.ArticleInfoItems> searchArticles(String query) {
		String clientId = naverConfig.getId();
		String clientSecret = naverConfig.getSecret();

		String keyword = URLEncoder.encode(query, StandardCharsets.UTF_8);

		URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/search/news.json")
				.queryParam("query", keyword).queryParam("display", 10).queryParam("start", 1).queryParam("sort", "sim")
				.encode().build().toUri();

		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Naver-Client-Id", clientId)
				.header("X-Naver-Client-Secret", clientSecret).build();

		try {
			ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

			if (resp.getStatusCode() != HttpStatus.OK) {
				log.error("네이버 API 응답 오류: HTTP 상태 코드 {}", resp.getStatusCode());
				throw new CommonException(NaverApiErrorCode.NAVER_API_UNKNOWN_ERROR);
			}

			if (Objects.isNull(resp.getBody()) || resp.getBody().isEmpty()) {
				log.error("네이버 API 응답 바디가 비어 있음");
				throw new CommonException(NaverApiErrorCode.NAVER_API_EMPTY_RESPONSE);
			}

			return objectMapper.readValue(resp.getBody(), ArticleInfoResponse.class).getItems();

		} catch (HttpClientErrorException e) {
			log.error("클라이언트 오류 (4xx): {}", e.getMessage());
			throw new CommonException(NaverApiErrorCode.NAVER_API_CLIENT_ERROR);
		} catch (HttpServerErrorException e) {
			log.error("서버 오류 (5xx): {}", e.getMessage());
			throw new CommonException(NaverApiErrorCode.NAVER_API_SERVER_ERROR);
		} catch (UnknownHttpStatusCodeException e) {
			log.error("알 수 없는 HTTP 상태 코드 응답: {}", e.getMessage());
			throw new CommonException(NaverApiErrorCode.NAVER_API_UNKNOWN_ERROR);
		} catch (JsonProcessingException e) {
			log.error("JSON 파싱 오류: {}", e.getMessage());
			throw new CommonException(NaverApiErrorCode.NAVER_API_JSON_ERROR);
		} catch (Exception e) {
			log.error("예상치 못한 오류 발생: {}", e.getMessage());
			throw new CommonException(NaverApiErrorCode.NAVER_API_UNKNOWN_ERROR);
		}
	}
}
