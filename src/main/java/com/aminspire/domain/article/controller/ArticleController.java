package com.aminspire.domain.article.controller;

import com.aminspire.domain.article.domain.Article;
import com.aminspire.domain.article.dto.response.ArticleInfoResponse;
import com.aminspire.domain.article.service.ArticleService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aminspire.global.common.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:8080"})
@RequestMapping("/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    // 기사 검색
    @GetMapping("/search")
    public CommonResponse<?> searchArticles(@RequestParam(value = "query", required = true) String query) {
        List<ArticleInfoResponse.ArticleInfoItems> results = articleService.searchArticles(query);
        return CommonResponse.onSuccess(HttpStatus.OK.value(), results); // 200 OK
    }

    // 특정 유저의 스크랩 저장
    @PostMapping("/scrap")
    public CommonResponse<Map<String, String>> scrapArticle(
            @RequestHeader("Authorization") String token,
            @RequestBody ArticleInfoResponse.ArticleInfoItems articleInfoItems) {
        Map<String, String> result = new HashMap<>();
        // 스크랩 기사 저장
        articleService.saveArticle(token, articleInfoItems);
        result.put("message", "기사 스크랩 성공!");

        return CommonResponse.onSuccess(HttpStatus.CREATED.value(), result); // 201 CREATED
    }

    // 특정 유저의 스크랩 조회
    @GetMapping("/scrap")
    public CommonResponse<?> getScrapedArticles(@RequestHeader("Authorization") String token) {
        List<Article> scrapedArticles = articleService.getArticlesByUser(token);
        return CommonResponse.onSuccess(HttpStatus.OK.value(), scrapedArticles); // 200 OK
    }

    // 특정 유저의 스크랩 삭제
    @DeleteMapping("/scrap")
    public CommonResponse<Map<String, String>> deleteScrapArticle(
            @RequestHeader("Authorization") String token,
            @RequestParam Long newsId) {
        Map<String, String> result = new HashMap<>();

        articleService.deleteScrap(token, newsId);
        result.put("message", "기사 스크랩 삭제 성공!");
        return CommonResponse.onSuccess(HttpStatus.OK.value(), result); // 200 OK
    }
}
