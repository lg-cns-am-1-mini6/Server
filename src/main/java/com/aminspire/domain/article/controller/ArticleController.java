package com.aminspire.domain.article.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.service.ArticleService;

@RestController
@CrossOrigin(origins = "*") // 모든 도메인에서 접근 허용
@RequestMapping("/articles")
public class ArticleController {

	private final ArticleService articleService;

	// 생성자 주입 방식 사용
	public ArticleController(ArticleService articleService) {
		this.articleService = articleService;
	}

	// 키워드 기반 기사 검색
	@GetMapping("/search")
	public ResponseEntity<List<ArticleInfoResponse.ArticleInfoItems>> searchArticles(
			@RequestParam("query") String query) {
		return ResponseEntity.ok(articleService.searchArticles(query));
	}

}
