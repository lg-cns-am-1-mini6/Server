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

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "*")  // 모든 도메인에서 접근 허용
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

	private final ArticleService articleService;

	/// 키워드 기반 기사 검색
    @GetMapping("/search")
    public ResponseEntity<List<ArticleInfoResponse>> searchArticles(
            @RequestParam("keyword") String keyword, 
            @RequestParam(value = "country", defaultValue = "kr,us") String country) {

        return ResponseEntity.ok(articleService.searchArticles(keyword, country));
    }

	// 전체 기사 조회
	@GetMapping("/searchAll")
	public ResponseEntity<List<ArticleInfoResponse>> getAllArticles() {
		// 모든 기사를 반환
		List<ArticleInfoResponse> allArticles = articleService.getAllArticles();
		return ResponseEntity.ok(allArticles);
	}
}
