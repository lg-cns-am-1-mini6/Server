package com.aminspire.domain.article.controller;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.service.ArticleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aminspire.domain.user.repository.UserRepository;
import com.aminspire.global.common.response.CommonResponse;
import com.aminspire.global.exception.CommonException;
import com.aminspire.infra.config.feign.NaverFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private NaverFeignClient naverFeignClient;
    @Autowired
    private UserRepository userRepository;

    // 기사 검색
    @GetMapping("/search")
    public CommonResponse<?> searchArticles(@RequestParam(value = "query", required = true) String query) {
        try {
            List<ArticleInfoResponse.ArticleInfoItems> results = articleService.searchArticles(query);
            return CommonResponse.onSuccess(HttpStatus.OK.value(), results); // 200 OK
        } catch (CommonException e) {
            log.error("기사 검색 실패: {}", e.getMessage(), e);

            String errorMessage = "기사 검색 실패: " + e.getMessage();
            return CommonResponse.onFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage); // 500 INTERNAL_SERVER_ERROR
        }
    }

    // 특정 유저의 스크랩 저장
    @PostMapping("/scrap")
    public CommonResponse<Map<String, String>> scrapArticle(
            @RequestParam Long userId,
            @RequestBody ArticleInfoResponse.ArticleInfoItems articleInfoItems) {
        Map<String, String> result = new HashMap<>();

        try {
            // 스크랩 기사 저장
            articleService.saveArticle(userId, articleInfoItems);
            result.put("message", "기사 스크랩 성공!");

            return CommonResponse.onSuccess(HttpStatus.CREATED.value(), result); // 201 CREATED
        } catch (CommonException e) {
            log.error("기사 스크랩 실패: {}", e.getMessage(), e);
            return CommonResponse.onFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), result); // 500 INTERNAL_SERVER_ERROR
        }
    }

    // 특정 유저의 스크랩 조회
    @GetMapping("/scrap")
    public CommonResponse<?> getScrapedArticles(@RequestParam Long userId) {
        try {
            List<Article> scrapedArticles = articleService.getArticlesByUser(userId);
            return CommonResponse.onSuccess(HttpStatus.OK.value(), scrapedArticles); // 200 OK
        } catch (CommonException e) {
            log.error("스크랩 조회 실패: {}", e.getMessage(), e);
            return CommonResponse.onFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()); // 500 INTERNAL_SERVER_ERROR
        }
    }

    // 특정 유저의 스크랩 삭제
    @DeleteMapping("/scrap")
    public CommonResponse<Map<String, String>> deleteScrapArticle(
            @RequestParam Long userId,
            @RequestParam Long newsId) {
        Map<String, String> result = new HashMap<>();

        try {
            articleService.deleteScrap(userId, newsId);
            result.put("message", "기사 스크랩 삭제 성공!");
            return CommonResponse.onSuccess(HttpStatus.OK.value(), result); // 200 OK
        } catch (CommonException e) {
            log.error("기사 스크랩 삭제 실패: {}", e.getMessage(), e);
            result.put("message", "기사 스크랩 삭제 실패");
            result.put("description", e.getMessage());
            return CommonResponse.onFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), result); // 500 INTERNAL_SERVER_ERROR
        }
    }
}
